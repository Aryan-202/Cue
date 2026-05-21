package com.music.cue.org.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.music.cue.org.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.recording))
    
    val iconOffset = remember { Animatable(0f) }
    val lottieOffset = remember { Animatable(0f) }
    val lottieAlpha = remember { Animatable(0f) }
    // Start at 1f (fully visible) to seamlessly transition from the system splash screen
    val iconScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        // Start the split animation almost immediately after taking over from the system
        delay(300) 
        
        launch {
            iconOffset.animateTo(
                targetValue = -80f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
        launch {
            lottieAlpha.animateTo(1f, tween(durationMillis = 500))
            lottieOffset.animateTo(
                targetValue = 80f,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
        
        delay(2000) // Watch the animation for a bit
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A237E), // Deep Blue
                        Color(0xFF0D47A1), // Blue
                        Color(0xFF01579B)  // Light Blue
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // App Icon - Matches the system splash screen icon for a seamless transition
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .offset(x = iconOffset.value.dp)
                .alpha(iconScale.value)
        )
        
        // Lottie Animation
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = lottieOffset.value.dp)
                .alpha(lottieAlpha.value)
        ) {
            LottieAnimation(
                composition = composition,
                iterations = LottieConstants.IterateForever,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
