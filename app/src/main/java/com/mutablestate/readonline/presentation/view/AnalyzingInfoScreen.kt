package com.mutablestate.readonline.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.LiveData
import com.airbnb.lottie.compose.*
import com.mutablestate.readonline.R
import com.mutablestate.readonline.presentation.composable.TransitionAnimation

@Composable
fun AnalyzingInfoScreen(tagLost: LiveData<Boolean>, resetTagLostState: () -> Unit) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.teal_square))
    val nfcTagLost = tagLost.observeAsState().value

    TransitionAnimation(true) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(50.dp)
        ) {
            Text(
                text = stringResource(R.string.reading_id_message_analyzing_info_screen),
                fontSize = 20.sp,
                modifier = Modifier.weight(.3f),
                textAlign = TextAlign.Center
            )
            LottieAnimation(
                composition = composition,
                iterations = Int.MAX_VALUE,
                modifier = Modifier.weight(.7f)
            )
        }
    }
    if (nfcTagLost == true) {
        NFCTagLostDialog(resetTagLostState)
    }
}

@Composable
fun NFCTagLostDialog(resetTagLostState: () -> Unit) {
    Dialog(
        onDismissRequest = { resetTagLostState.invoke() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.White)
                .padding(30.dp)
        ) {
            Text(
                text = stringResource(R.string.analyze_screen_lost_tag),
                textAlign = TextAlign.Justify
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    resetTagLostState.invoke()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.analyze_screen_lost_tag_btn))
            }
        }
    }
}