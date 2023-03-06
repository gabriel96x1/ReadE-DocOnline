package com.mutablestate.readonline.presentation.view

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.mutablestate.readonline.presentation.activities.CameraActivity

@Composable
fun InstructionsScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to e-Docs Analyzer",
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "You're about to read the NFC chip of your e-ID or e-Passport",
            fontSize = 20.sp,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "To complete this please follow the next recommendations:",
            fontSize = 20.sp,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = " - Try to find a place with a good illumination",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = " - Center your ID inside the white rectangle",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = " - Press the capture button to start analyzing your ID information",
            fontSize = 14.sp,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Please press \"Take a pic of your ID\" to continue",
            fontSize = 20.sp,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(30.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val intent = Intent(context, CameraActivity::class.java)
                    startActivity(context, intent, null)
                }
            ) {
                Text(text = "Take a pic of your ID")
            }
        }
    }
}