package com.music.cue.org.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.music.cue.org.R

object CueIcons {
    val PlayCircle: Painter
        @Composable
        get() = painterResource(id = R.drawable.play_circle)

    val PauseCircle: Painter
        @Composable
        get() = painterResource(id = R.drawable.pause_circle)
        
    val AppIcon: Painter
        @Composable
        get() = painterResource(id = R.drawable.ic_launcher_foreground)

    val SkipNext: ImageVector = Icons.Default.SkipNext
    val SkipPrevious: ImageVector = Icons.Default.SkipPrevious
    val ArrowBack: ImageVector = Icons.AutoMirrored.Filled.ArrowBack
    val FastForward: ImageVector = Icons.Default.FastForward
    val FastRewind: ImageVector = Icons.Default.FastRewind
    val MusicNote: ImageVector = Icons.Default.MusicNote
}
