package com.mutablestate.readonline.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.mutablestate.readonline.domain.models.UserChipInfo

@Composable
fun ResultsScreen(chipInfo: UserChipInfo?) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            bitmap = chipInfo!!.photo.asImageBitmap(),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = chipInfo.primaryId)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = chipInfo.secondId)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = chipInfo.gender)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = chipInfo.issuingState)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = chipInfo.nationality)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = chipInfo.ChipAuth)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = chipInfo.passiveAuth)
    }


}