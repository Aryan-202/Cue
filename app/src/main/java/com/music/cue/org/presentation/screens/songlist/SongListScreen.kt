package com.music.cue.org.presentation.screens.songlist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.music.cue.org.domain.model.Song


@Composable
fun SongListScreen(
    viewModel: SongListViewModel = hiltViewModel(),
    onSongClick: (Song) -> Unit
) {
    val state by viewModel.state.collectAsState()

}

@Composable
@Preview
fun SongListScreenPreview() {
    SongListScreen(onSongClick = {})
}
