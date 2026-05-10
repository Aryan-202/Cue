package com.music.cue.org.screens.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun BottomPlayer(
    modifier: Modifier = Modifier,
    trackName: String = "Not Playing",
    artistName: String = "",
    isPlaying: Boolean = false,
    onTogglePlay: () -> Unit = {},
    onSkipPrevious: () -> Unit = {},
    onSkipNext: () -> Unit = {},
    onSwipeUp: () -> Unit = {},
    onSwipeDown: () -> Unit = {}
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var animationDirection by remember { mutableIntStateOf(1) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(72.dp)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    },
                    onDragEnd = {
                        if (abs(offsetX) > abs(offsetY)) {
                            if (offsetX > 100) {
                                animationDirection = -1
                                onSkipPrevious()
                            } else if (offsetX < -100) {
                                animationDirection = 1
                                onSkipNext()
                            }
                        } else {
                            if (offsetY < -100) onSwipeUp()
                            else if (offsetY > 100) onSwipeDown()
                        }
                        offsetX = 0f
                        offsetY = 0f
                    }
                )
            },
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp
    ) {
        AnimatedContent(
            targetState = trackName,
            transitionSpec = {
                if (animationDirection > 0) {
                    (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                            (slideOutHorizontally { width -> -width } + fadeOut())
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                            (slideOutHorizontally { width -> width } + fadeOut())
                }
            },
            label = "TrackChange"
        ) { targetTrack ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = targetTrack, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
                    if (artistName.isNotEmpty()) {
                        Text(text = artistName, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                    }
                }
                IconButton(onClick = {
                    animationDirection = -1
                    onSkipPrevious()
                }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Skip Previous")
                }
                IconButton(onClick = onTogglePlay) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }
                IconButton(onClick = {
                    animationDirection = 1
                    onSkipNext()
                }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Skip Next")
                }
            }
        }
    }
}
