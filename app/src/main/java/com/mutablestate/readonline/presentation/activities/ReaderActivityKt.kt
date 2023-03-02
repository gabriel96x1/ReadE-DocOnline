package com.mutablestate.readonline.presentation.activities

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Base64
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mutablestate.readonline.domain.models.UserChipInfo
import com.mutablestate.readonline.domain.utils.ImageUtil
import com.mutablestate.readonline.domain.utils.ReadingUtils
import com.mutablestate.readonline.presentation.stateflows.ReadingNFCState
import com.mutablestate.readonline.presentation.view.AnalyzingInfoScreen
import com.mutablestate.readonline.presentation.view.HoldCloseDocScreen
import com.mutablestate.readonline.presentation.view.ResultsScreen
import com.mutablestate.readonline.presentation.viewmodel.ReaderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sf.scuba.smartcards.CardService
import org.apache.commons.io.IOUtils
import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.ASN1Primitive
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.ASN1Set
import org.bouncycastle.asn1.x509.Certificate
import org.jmrtd.BACKey
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


class ReaderActivityKt : ComponentActivity() {

    val TAG = ReaderActivityKt::class.java.simpleName

    val KEY_PASSPORT_NUMBER = "passportNumber"
    val KEY_EXPIRATION_DATE = "expirationDate"
    val KEY_BIRTH_DATE = "birthDate"

    lateinit var dg1File: DG1File
    lateinit var dg2File: DG2File
    lateinit var dg14File: DG14File
    lateinit var sodFile: SODFile
    lateinit var imageBase64: String
    lateinit var bitmap: Bitmap
    private var chipAuthSucceeded = false
    private var passiveAuthSuccess = false

    private var dg14Encoded = ByteArray(0)

    private var encodePhotoToBase64 = false
    private var passportNumberFromIntent = false

    private lateinit var preferences : SharedPreferences
    private lateinit var viewModel : ReaderViewModel

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        viewModel = ViewModelProvider(this).get(ReaderViewModel::class.java)

        val dateOfBirth = intent.getStringExtra("birthDate")
        val dateOfExpiry = intent.getStringExtra("expiryDate")
        val passportNumber = intent.getStringExtra("docNum")
        encodePhotoToBase64 = intent.getBooleanExtra("photoAsBase64", false)

        if (dateOfBirth != null) {
            preferences
                .edit().putString(KEY_BIRTH_DATE, dateOfBirth).apply()
        }
        if (dateOfExpiry != null) {
            preferences
                .edit().putString(KEY_EXPIRATION_DATE, dateOfExpiry).apply()
        }
        if (passportNumber != null) {
            preferences
                .edit().putString(KEY_PASSPORT_NUMBER, passportNumber).apply()
            passportNumberFromIntent = true
        }
        Log.e(TAG, "$passportNumber $dateOfExpiry $dateOfBirth")

        setContent {
            val readingState = viewModel.readingState.observeAsState().value
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) {

                when (readingState) {
                    ReadingNFCState.PREREAD -> {
                        HoldCloseDocScreen()
                    }
                    ReadingNFCState.READING -> {
                        AnalyzingInfoScreen()
                    }
                    ReadingNFCState.ENDREAD -> {
                        ResultsScreen(
                            viewModel.userChipInfo.value
                        )
                    }
                    else -> HoldCloseDocScreen()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val adapter = NfcAdapter.getDefaultAdapter(this)
        if (adapter != null) {
            val intent = Intent(applicationContext, this.javaClass)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            val filter = arrayOf(arrayOf("android.nfc.tech.IsoDep"))
            adapter.enableForegroundDispatch(this, pendingIntent, null, filter)
        }

    }

    override fun onPause() {
        super.onPause()

        val adapter = NfcAdapter.getDefaultAdapter(this)
        adapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent!!.action) {
            val tag = intent.extras!!.getParcelable<Tag>(NfcAdapter.EXTRA_TAG)
            if (Arrays.asList(*tag!!.techList).contains("android.nfc.tech.IsoDep")) {
                val passportNumber = preferences.getString(KEY_PASSPORT_NUMBER, null)
                val expirationDate = ReadingUtils.convertDate(
                    preferences.getString(
                        KEY_EXPIRATION_DATE,
                        null
                    )
                )
                val birthDate = ReadingUtils.convertDate(
                    preferences.getString(
                        KEY_BIRTH_DATE,
                        null
                    )
                )
                Log.e(TAG, "$passportNumber $expirationDate $birthDate")

                viewModel.startReadNfc()

                if (!passportNumber.isNullOrEmpty() && expirationDate != null && expirationDate.isNotEmpty() && birthDate != null && birthDate.isNotEmpty()) {
                    val bacKey: BACKeySpec = BACKey(passportNumber, birthDate, expirationDate)
                    ReadTask(IsoDep.get(tag), bacKey)

                } else {
                    Log.e(TAG, "erroooor en backey")
                }
            }
        }

    }

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
                    ASN1InputStream(getAssets().open("masterList"))
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

    private fun ReadTask(isoDep: IsoDep?, bacKey: BACKeySpec) {
        lifecycleScope.launch(Dispatchers.Default) {

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
                    applicationContext, faceImageInfo.mimeType, inputStream
                )
                imageBase64 = Base64.encodeToString(buffer, Base64.DEFAULT)
            }

            withContext(Dispatchers.Main) {

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

                val ratio = 320.0 / bitmap.height
                val targetHeight = (bitmap.height * ratio).toInt()
                val targetWidth = (bitmap.width * ratio).toInt()
                val photo: Bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false)
                viewModel.updateUserChipInfo(
                    UserChipInfo(
                        mrzInfo.primaryIdentifier.replace("<", " "),
                        mrzInfo.secondaryIdentifier.replace("<", " "),
                        mrzInfo.gender.toString(),
                        mrzInfo.issuingState,
                        mrzInfo.nationality,
                        passiveAuthStr,
                        chipAuthStr,
                        photo
                    )
                )

                viewModel.finishReadNfc()
            }
        }
    }
}