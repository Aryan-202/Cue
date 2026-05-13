package com.music.cue.org.lottie

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.music.cue.org.R

@Composable
fun NowPlayingAnimation(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = true
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.recording))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = isPlaying
    )

    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(32.dp)
    )
}
