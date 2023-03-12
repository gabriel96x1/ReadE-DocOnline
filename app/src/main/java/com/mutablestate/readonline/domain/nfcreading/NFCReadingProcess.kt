package com.mutablestate.readonline.domain.nfcreading

import android.content.Context
import android.graphics.Bitmap
import android.nfc.tech.IsoDep
import android.util.Base64
import android.util.Log
import com.mutablestate.readonline.domain.models.UserChipInfo
import com.mutablestate.readonline.domain.utils.ImageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.sf.scuba.smartcards.CardService
import org.apache.commons.io.IOUtils
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1Primitive
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.ASN1Set
import org.bouncycastle.asn1.x509.Certificate
import org.jmrtd.BACKeySpec
import org.jmrtd.PassportService
import org.jmrtd.lds.ChipAuthenticationPublicKeyInfo
import org.jmrtd.lds.SODFile
import org.jmrtd.lds.SecurityInfo
import org.jmrtd.lds.icao.DG14File
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG2File
import org.jmrtd.lds.iso19794.FaceImageInfo
import org.jmrtd.lds.iso19794.FaceInfo
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.InputStream
import java.security.KeyStore
import java.security.MessageDigest
import java.security.Signature
import java.security.cert.CertPathValidator
import java.security.cert.CertificateFactory
import java.security.cert.PKIXParameters
import java.security.cert.X509Certificate
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PSSParameterSpec
import java.util.*

class NFCReadingProcess(
    val context: Context
) {

    private val TAG = NFCReadingProcess::class.java.simpleName
    private lateinit var dg1File: DG1File
    private lateinit var dg2File: DG2File
    private lateinit var dg14File: DG14File
    private lateinit var sodFile: SODFile
    private lateinit var imageBase64: String
    private lateinit var bitmap: Bitmap
    private var chipAuthSucceeded = false
    private var passiveAuthSuccess = false

    private var dg14Encoded = ByteArray(0)

    private fun doChipAuth(service: PassportService) {
        try {
            val dg14In = service.getInputStream(PassportService.EF_DG14)
            dg14Encoded = IOUtils.toByteArray(dg14In)
            val dg14InByte = ByteArrayInputStream(dg14Encoded)
            dg14File = DG14File(dg14InByte)
            val dg14FileSecurityInfos: Collection<SecurityInfo> = dg14File.getSecurityInfos()
            for (securityInfo in dg14FileSecurityInfos) {
                if (securityInfo is ChipAuthenticationPublicKeyInfo) {
                    val publicKeyInfo = securityInfo
                    val keyId = publicKeyInfo.keyId
                    val publicKey = publicKeyInfo.subjectPublicKey
                    val oid = publicKeyInfo.objectIdentifier
                    service.doEACCA(
                        keyId,
                        ChipAuthenticationPublicKeyInfo.ID_CA_ECDH_AES_CBC_CMAC_256,
                        oid,
                        publicKey
                    )
                    chipAuthSucceeded = true
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
    }

    private fun doPassiveAuth() {
        try {
            val digest = MessageDigest.getInstance(sodFile.getDigestAlgorithm())
            val dataHashes: Map<Int, ByteArray> = sodFile.getDataGroupHashes()
            var dg14Hash: ByteArray? = ByteArray(0)
            if (chipAuthSucceeded) {
                dg14Hash = digest.digest(dg14Encoded)
            }
            val dg1Hash = digest.digest(dg1File.getEncoded())
            val dg2Hash = digest.digest(dg2File.getEncoded())
            if (Arrays.equals(dg1Hash, dataHashes[1]) && Arrays.equals(
                    dg2Hash,
                    dataHashes[2]
                ) && (!chipAuthSucceeded || Arrays.equals(dg14Hash, dataHashes[14]))
            ) {
                // We retrieve the CSCA from the german master list
                val asn1InputStream: ASN1InputStream =
                    ASN1InputStream(context                                  .getAssets().open("masterList"))
                var p: ASN1Primitive?
                val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
                keystore.load(null, null)
                val cf = CertificateFactory.getInstance("X.509")
                while (asn1InputStream.readObject().also { p = it } != null) {
                    val asn1 = ASN1Sequence.getInstance(p)
                    require(!(asn1 == null || asn1.size() == 0)) { "null or empty sequence passed." }
                    require(asn1.size() == 2) { "Incorrect sequence size: " + asn1.size() }
                    val certSet = ASN1Set.getInstance(asn1.getObjectAt(1))
                    for (i in 0 until certSet.size()) {
                        val certificate = Certificate.getInstance(certSet.getObjectAt(i))
                        val pemCertificate = certificate.encoded
                        val javaCertificate =
                            cf.generateCertificate(ByteArrayInputStream(pemCertificate))
                        keystore.setCertificateEntry(i.toString(), javaCertificate)
                    }
                }
                val docSigningCertificates: List<X509Certificate> =
                    sodFile.getDocSigningCertificates()
                for (docSigningCertificate in docSigningCertificates) {
                    docSigningCertificate.checkValidity()
                }

                // We check if the certificate is signed by a trusted CSCA
                // TODO: verify if certificate is revoked
                val cp = cf.generateCertPath(docSigningCertificates)
                val pkixParameters = PKIXParameters(keystore)
                pkixParameters.isRevocationEnabled = false
                val cpv = CertPathValidator.getInstance(CertPathValidator.getDefaultType())
                cpv.validate(cp, pkixParameters)
                var sodDigestEncryptionAlgorithm: String =
                    sodFile.getDocSigningCertificate().getSigAlgName()
                var isSSA = false
                if (sodDigestEncryptionAlgorithm == "SSAwithRSA/PSS") {
                    sodDigestEncryptionAlgorithm = "SHA256withRSA/PSS"
                    isSSA = true
                }
                val sign = Signature.getInstance(sodDigestEncryptionAlgorithm)
                if (isSSA) {
                    sign.setParameter(
                        PSSParameterSpec(
                            "SHA-256",
                            "MGF1",
                            MGF1ParameterSpec.SHA256,
                            32,
                            1
                        )
                    )
                }
                sign.initVerify(sodFile.getDocSigningCertificate())
                sign.update(sodFile.getEContent())
                passiveAuthSuccess = sign.verify(sodFile.getEncryptedDigest())
            }
        } catch (e: Exception) {
            Log.w(TAG, e)
        }
    }

    suspend fun readTask(
        isoDep: IsoDep?,
        bacKey: BACKeySpec
    ): UserChipInfo {
        val cardService = CardService.getInstance(isoDep)
        cardService.open()

        val service = PassportService(
            cardService,
            PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
            PassportService.DEFAULT_MAX_BLOCKSIZE,
            false,
            false
        )
        service.open()

        val paceSucceeded = false

        service.sendSelectApplet(paceSucceeded)

        if (!paceSucceeded) {
            try {
                service.getInputStream(PassportService.EF_COM).read()
            } catch (e: Exception) {
                service.doBAC(bacKey)
            }
        }

        val dg1In = service.getInputStream(PassportService.EF_DG1)
        dg1File = DG1File(dg1In)

        val dg2In = service.getInputStream(PassportService.EF_DG2)
        dg2File = DG2File(dg2In)

        val sodIn = service.getInputStream(PassportService.EF_SOD)
        sodFile = SODFile(sodIn)

        // We perform Chip Authentication using Data Group 14
        doChipAuth(service)

        // Then Passive Authentication using SODFile
        doPassiveAuth()

        val allFaceImageInfos: MutableList<FaceImageInfo> = ArrayList()
        val faceInfos: List<FaceInfo> = dg2File.getFaceInfos()
        for (faceInfo in faceInfos) {
            allFaceImageInfos.addAll(faceInfo.faceImageInfos)
        }

        if (!allFaceImageInfos.isEmpty()) {
            val faceImageInfo = allFaceImageInfos.iterator().next()
            val imageLength = faceImageInfo.imageLength
            val dataInputStream = DataInputStream(faceImageInfo.imageInputStream)
            val buffer = ByteArray(imageLength)
            withContext(Dispatchers.IO) {
                dataInputStream.readFully(buffer, 0, imageLength)
            }
            val inputStream: InputStream = ByteArrayInputStream(buffer, 0, imageLength)
            bitmap = ImageUtil.decodeImage(
                context, faceImageInfo.mimeType, inputStream
            )
            imageBase64 = Base64.encodeToString(buffer, Base64.DEFAULT)
        }

        return postReadingTask()
    }

    private fun postReadingTask(): UserChipInfo {
        val mrzInfo = dg1File.mrzInfo

        val passiveAuthStr = if (passiveAuthSuccess) {
            "passed"
        } else {
            "failed"
        }

        val chipAuthStr = if (chipAuthSucceeded) {
            "passed"
        } else {
            "failed"
        }

        mrzInfo.dateOfBirth
        val ratio = 320.0 / bitmap.height
        val targetHeight = (bitmap.height * ratio).toInt()
        val targetWidth = (bitmap.width * ratio).toInt()
        val photo: Bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)

        return UserChipInfo(
            mrzInfo.primaryIdentifier.replace("<", " "),
            mrzInfo.secondaryIdentifier.replace("<", " "),
            mrzInfo.gender.toString(),
            mrzInfo.issuingState,
            mrzInfo.nationality,
            mrzInfo.documentNumber,
            mrzInfo.dateOfExpiry,
            passiveAuthStr,
            chipAuthStr,
            photo
        )
    }

}