package com.music.cue.org.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.music.cue.org.MusicPlayerViewModel
import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueShapes

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MusicPlayerViewModel = viewModel()
) {
    val currentSong by viewModel.currentSong.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Bar
        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp)
                .border(1.dp, CueColors.MediumGray, shape = CueShapes.medium)
                .clip(CueShapes.medium)
        )

        // Song List
        SongListScreen(viewModel = viewModel)

        // Mini Player (if a song is playing)
        if (currentSong != null) {
            MiniPlayer(
                song = currentSong!!,
                isPlaying = viewModel.isPlaying.collectAsState().value,
                onPlayPause = { viewModel.playPause() },
                onNext = { viewModel.skipToNext() },
                onPrevious = { viewModel.skipToPrevious() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreen()
}

