package com.mutablestate.readonline.presentation.view

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
    readerActivityKt: ReaderActivityKt?
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
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        elevation = 5.dp
                    ) {
                        if (chipInfo.photo != null) {
                            Image(
                                bitmap = chipInfo.photo.asImageBitmap(),
                                contentDescription = stringResource(R.string.photo_description_results_screen)
                            )
                        } else {
                            Icon(painter = painterResource(R.drawable.ic_launcher_foreground), contentDescription = null)
                        }
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
                    Spacer(modifier = Modifier.height(4.dp))
                    chipInfo.extraInfo?.let {
                        it.custodyInformation?.let { it1 ->
                            Text(
                                text = it1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        chipInfo.extraInfo.fullDateOfBirth?.let { it1 ->
                            Text(
                                text = it1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        chipInfo.extraInfo.profession?.let { it1 ->
                            Text(
                                text = it1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        chipInfo.extraInfo.personalNumber?.let { it1 ->
                            Text(
                                text = it1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        chipInfo.extraInfo.personalSummary?.let { it1 ->
                            Text(
                                text = it1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        chipInfo.extraInfo.telephone?.let { it1 ->
                            Text(
                                text = it1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        chipInfo.extraInfo.permanentAddress?.let { it1 ->
                            Text(
                                text = it1.toString()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        chipInfo.extraInfo.nameOfHolder?.let { it1 ->
                            Text(
                                text = it1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        chipInfo.extraInfo.otherNames?.let { it1 ->
                            Text(
                                text = it1.toString()
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        chipInfo.extraInfo.otherValidTDNumbers?.let { it1 ->
                            Text(
                                text = it1.toString()
                            )
                        }
                    }
                    chipInfo.signature?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Card(
                            elevation = 5.dp
                        ) {
                            Image(
                                bitmap = BitmapFactory.decodeStream(it.images.first().imageInputStream).asImageBitmap(),
                                contentDescription = stringResource(R.string.photo_description_results_screen)
                            )
                        }
                    }
                    chipInfo.issuer?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        it.dateOfIssue?.let { it1 ->
                            Text(
                                text = it1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        it.issuingAuthority?.let { it1 ->
                            Text(
                                text = it1
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        it.taxOrExitRequirements?.let { it1 ->
                            Text(
                                text = it1
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if(isReal) stringResource(R.string.real_id_results_screen)
                        else stringResource(R.string.false_id_results_screen),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(60.dp))
                    Button(
                        onClick = { readerActivityKt?.finish() }
                    ) {
                        Text(text = stringResource(R.string.back_to_start_results_screen))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun ResultsScreenPreview() {
    ResultsScreen(
        UserChipInfo(
            "Androide",
            "Number 1",
            "NA",
            "holi",
            "Del otro lado del charco",
            "holi",
            "holi",
            "holi",
            "holi",
            null,
            null,
            null,
            null,
        ),
        "Some Text Here",
        null,
    )
}

