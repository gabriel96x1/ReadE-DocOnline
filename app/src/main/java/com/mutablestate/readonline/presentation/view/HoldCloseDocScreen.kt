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
            text = "Hold your ID close to the back of your device",
            fontSize = 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        Icon(
            painter = painterResource(id = R.drawable.nfc),
            contentDescription = "NFC",
            modifier = Modifier.size(60.dp)
        )
    }

    if (!nfcEnabled.value) {
        NFCDialog(nfcEnabled, context)
    }
}

@Composable
fun NFCDialog(nfcAdapter: MutableState<Boolean>, context: Context) {
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
                text = "Hey there, to continue you need enable your NFC before",
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
                Text(text = "To NFC Settings")
            }
        }
    }

}