package com.music.cue.org.presentation.components.items

import android.content.ContentUris
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.cue.org.R
import com.music.cue.org.v1.Song
import androidx.core.net.toUri

@Composable
fun SongListItem(
    song: Song,
    onClick: () -> Unit
) {
    val albumArtUri = ContentUris.withAppendedId(
        "content://media/external/audio/albumart".toUri(),
        song.albumId
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable{onClick()}
            .padding(72.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = albumArtUri,
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFEBEBEB)),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.app_icon)

        )
    }
}