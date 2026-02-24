package com.music.cue.org.presentation.components



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.cue.org.data.model.Song
import com.music.cue.org.theme.CueColors

@Composable
fun SongItem(
    song: Song,
    isPlaying: Boolean = false,
    isFavorite: Boolean = false,
    onSongClick: () -> Unit,
    onFavoriteClick: (() -> Unit)? = null,
    onMoreClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSongClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Art


        Spacer(modifier = Modifier.width(12.dp))

        // Song Info


        // Duration


        // Favorite Button (Optional)
        if (onFavoriteClick != null) {

        }

        // More Options Button (Optional)
        if (onMoreClick != null) {

        }
    }
}




