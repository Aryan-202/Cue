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
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.music.cue.org.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.ColorFilter
import com.music.cue.org.ui.theme.CueIcons

@Composable
fun SplashScreen(onAnimationFinished: () -> Unit) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.recording))
    
    val iconOffset = remember { Animatable(0f) }
    val lottieOffset = remember { Animatable(0f) }
    val lottieAlpha = remember { Animatable(0f) }
    val iconScale = remember { Animatable(1f) }

    val isDark = isSystemInDarkTheme()
    
    // Dynamic background based on theme
    val backgroundColor = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF121212)))
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFD1E4FF), // Matching splash_background_color
                Color(0xFFFFFFFF)
            )
        )
    }

    LaunchedEffect(Unit) {
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
        delay(2000)
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = CueIcons.AppIcon,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .offset(x = iconOffset.value.dp)
                .alpha(iconScale.value),
            colorFilter = ColorFilter.tint(if (isDark) Color.White else Color(0xFF1C274C))
        )
        
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
