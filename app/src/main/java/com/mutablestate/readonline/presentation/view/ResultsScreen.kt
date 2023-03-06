package com.mutablestate.readonline.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mutablestate.readonline.domain.models.UserChipInfo
import com.mutablestate.readonline.domain.utils.ReadingUtils

@Composable
fun ResultsScreen(chipInfo: UserChipInfo?, mlkitText: String?) {
    val isReal = ReadingUtils.isRealId(mlkitText!!, chipInfo!!)
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            bitmap = chipInfo.photo.asImageBitmap(),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Text(text = "Primary ID: ")
            Text(text = chipInfo.primaryId)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Text(text = "Secondary ID: ")
            Text(text = chipInfo.secondId)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Text(text = "Gender: ")
            Text(text = chipInfo.gender)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Text(text = "Nationality: ")
            Text(text = chipInfo.nationality)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row {
            Text(
                text = if(isReal) "Comparing printed information with chip information this ID is REAL"
                else "Comparing printed information with chip information this ID is FALSE",
                textAlign = TextAlign.Justify
            )
        }
    }
}