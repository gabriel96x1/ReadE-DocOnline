package com.mutablestate.readonline.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.mutablestate.readonline.R
import com.mutablestate.readonline.domain.models.UserChipInfo
import com.mutablestate.readonline.domain.utils.ReadingUtils
import com.mutablestate.readonline.presentation.activities.ReaderActivityKt
import com.mutablestate.readonline.presentation.composable.TransitionAnimation

@Composable
fun ResultsScreen(
    chipInfo: UserChipInfo?,
    mlkitText: String?,
    readerActivityKt: ReaderActivityKt
) {
    val isReal = ReadingUtils.isRealId(mlkitText!!, chipInfo!!)

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.circle_results_teal))

    TransitionAnimation(true) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                elevation = 10.dp,
                modifier = Modifier.fillMaxSize(.85f)
            ) {
                LottieAnimation(
                    composition = composition,
                    iterations = Int.MAX_VALUE,
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        elevation = 5.dp
                    ) {
                        Image(
                            bitmap = chipInfo.photo.asImageBitmap(),
                            contentDescription = stringResource(R.string.photo_description_results_screen)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.primary_id_results_screen)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chipInfo.primaryId
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.secondary_id_results_screen)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chipInfo.secondId
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.gender_results_screen)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chipInfo.gender
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.nationality_results_screen)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = chipInfo.nationality
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if(isReal) stringResource(R.string.real_id_results_screen)
                        else stringResource(R.string.false_id_results_screen),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(60.dp))
                    Button(
                        onClick = { readerActivityKt.finish() }
                    ) {
                        Text(text = stringResource(R.string.back_to_start_results_screen))
                    }
                }
            }
        }
    }
}
