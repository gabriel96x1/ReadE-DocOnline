package com.mutablestate.readonline.presentation.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnalyzingInfoScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val scale = scaleShapeTransition(0.1f, 1f, 2000)
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Reading your Id, don't move it.",
            fontSize = 24.sp,
            modifier = Modifier.weight(0.3f)
        )
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.7f)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            onDraw = {
                val canvasSize = size
                val canvasWidth = size.width
                val canvasHeight = size.height

                drawRoundRect(
                    size = canvasSize / 2F,
                    color = Color.Cyan,
                    topLeft = Offset(
                        x = canvasWidth / 4F,
                        y = canvasHeight / 3F
                    ),
                    style = Stroke(width = 16F)
                )
            }
        )
    }
}

@Composable
fun scaleShapeTransition(
    initialValue: Float,
    targetValue: Float,
    durationMillis: Int
): Float {
    val infiniteTransition = rememberInfiniteTransition()
    val scale: Float by infiniteTransition.animateFloat(
        initialValue = initialValue,
        targetValue = targetValue,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis),
            repeatMode = RepeatMode.Restart
        )
    )

    return scale
}

@Preview
@Composable
fun Preview() {
    AnalyzingInfoScreen()
}