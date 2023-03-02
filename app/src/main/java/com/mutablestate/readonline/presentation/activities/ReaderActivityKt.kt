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


                if (!passportNumber.isNullOrEmpty() && expirationDate != null && expirationDate.isNotEmpty() && birthDate != null && birthDate.isNotEmpty()) {
                    val bacKey: BACKeySpec = BACKey(passportNumber, birthDate, expirationDate)
                    viewModel.startReadNfc(this, IsoDep.get(tag), bacKey)

                } else {
                    Log.e(TAG, "erroooor en backey")
                }
            }
        }

    }
}