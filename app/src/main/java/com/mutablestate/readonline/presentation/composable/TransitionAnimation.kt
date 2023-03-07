package com.mutablestate.readonline.presentation.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun TransitionAnimation(visibility: Boolean, composable: @Composable () -> Unit ) {
    AnimatedVisibility(
        visible = visibility,
        // Set the start width to 20 (pixels), 0 by default
        enter = expandHorizontally { 20 },
        exit = shrinkHorizontally(
            // Overwrites the default animation with tween for this shrink animation.
            animationSpec = tween(200),
            // Shrink towards the end (i.e. right edge for LTR, left edge for RTL). The default
            // direction for the shrink is towards [Alignment.Start]
            shrinkTowards = Alignment.End,
        ) { fullWidth ->
            // Set the end width for the shrink animation to a quarter of the full width.
            fullWidth / 4
        }

    ) {
        composable.invoke()
    }
}