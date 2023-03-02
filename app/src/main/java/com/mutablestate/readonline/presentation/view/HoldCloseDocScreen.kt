package com.mutablestate.readonline.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mutablestate.readonline.R

@Composable
fun HoldCloseDocScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Hold your ID close to the back of your device")
        Spacer(modifier = Modifier.height(20.dp))
        Icon(painter = painterResource(id = R.drawable.nfc), contentDescription = "")
    }
}