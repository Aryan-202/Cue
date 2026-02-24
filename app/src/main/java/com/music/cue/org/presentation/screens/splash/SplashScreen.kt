package com.music.cue.org.presentation.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueIcons
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit = {}  // Callback when splash ends
) {
    // Animation states
    val scale = remember { Animatable(0.3f) }
    val alpha = remember { Animatable(0f) }

    // Get screen dimensions for responsive sizing
    val configuration = LocalConfiguration.current
    val iconSize = remember(configuration) {
        if (configuration.screenWidthDp > 600) 150.dp else 100.dp
    }

    // Start animation when screen loads
    LaunchedEffect(Unit) {
        // Fade in
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )

        // Scale up with bounce effect
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // Wait a moment then finish
        delay(1000)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CueColors.BluePrimary,
                        CueColors.BlueDark
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = CueIcons.AppIcon),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(iconSize)
                .scale(scale.value)
                .alpha(alpha.value)
        )
    }
}

// Preview with different screen sizes
@Preview(showBackground = true, name = "Splash Screen - Phone")
@Preview(showBackground = true, widthDp = 720, name = "Splash Screen - Tablet")
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}