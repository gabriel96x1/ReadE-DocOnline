package com.mutablestate.readonline.presentation.view

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.mutablestate.readonline.presentation.activities.CameraActivity
import com.mutablestate.readonline.presentation.activities.MainActivity

@Composable
fun InstructionsScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "You're about to read your e-ID or e-Passport",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Please press \"Take a pic of your ID\" to continue",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(30.dp))
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