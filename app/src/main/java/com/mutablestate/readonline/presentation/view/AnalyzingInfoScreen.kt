package com.mutablestate.readonline.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.mutablestate.readonline.R
import com.mutablestate.readonline.presentation.composable.TransitionAnimation

@Composable
fun AnalyzingInfoScreen() {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.teal_square))

    TransitionAnimation(true) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(50.dp)
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
}

@Preview
@Composable
fun Preview() {
    AnalyzingInfoScreen()
}