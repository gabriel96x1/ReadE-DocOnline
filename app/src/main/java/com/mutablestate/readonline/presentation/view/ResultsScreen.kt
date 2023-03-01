package com.mutablestate.readonline.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.asImageBitmap
import com.mutablestate.readonline.domain.models.UserChipInfo

@Composable
fun ResultsScreen(chipInfo: UserChipInfo?) {

    Column {
        Text(text = chipInfo!!.primaryId)
        Text(text = chipInfo.secondId)
        Text(text = chipInfo.gender)
        Text(text = chipInfo.issuingState)
        Text(text = chipInfo.nationality)
        Text(text = chipInfo.ChipAuth)
        Text(text = chipInfo.passiveAuth)
        Image(
            bitmap = chipInfo.photo.asImageBitmap(),
            contentDescription = ""
        )

    }


}