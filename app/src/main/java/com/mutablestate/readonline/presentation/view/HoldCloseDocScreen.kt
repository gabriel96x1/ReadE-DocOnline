package com.mutablestate.readonline.presentation.view

import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.startActivity
import com.mutablestate.readonline.R

@Composable
fun HoldCloseDocScreen() {
    val context = LocalContext.current
    val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    val nfcEnabled = remember {
        mutableStateOf(nfcAdapter.isEnabled)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.hold_close_doc_string_hold_close_doc_screen),
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        Icon(
            painter = painterResource(id = R.drawable.nfc),
            contentDescription = stringResource(R.string.nfc_icon_description_hold_close_doc_screen),
            modifier = Modifier.size(60.dp)
        )
    }
    if (!nfcEnabled.value) {
        NFCDialog(nfcEnabled, context)
    }
}

@Composable
private fun NFCDialog(nfcAdapter: MutableState<Boolean>, context: Context) {
    Dialog(
        onDismissRequest = { nfcAdapter.value = true }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.White)
                .padding(30.dp)
        ) {
            Text(
                text = stringResource(R.string.dialog_text_hold_close_doc_screen),
                textAlign = TextAlign.Justify
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                    startActivity(context, intent, null)
                    nfcAdapter.value = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.to_nfc_settings_hold_close_doc_screen))
            }
        }
    }
}