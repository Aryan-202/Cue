package com.music.cue.org.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.music.cue.org.data.model.Song
import com.music.cue.org.theme.CueColors

@Composable
private fun SongInfo(
    song: Song,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = song.title,
            fontSize = 16.sp,
            fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
            color = if (isPlaying) CueColors.BluePrimary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = song.artist,
            fontSize = 14.sp,
            color = CueColors.MediumGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}