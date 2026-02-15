package com.music.cue.org.presentation.screens.songlist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.music.cue.org.domain.model.Song


@Composable
fun SongListScreen(
    modifier: Modifier = Modifier,
    viewModel: SongListViewModel = hiltViewModel(),
    onSongClick: (Song) -> Unit,
) {
    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
    ) {

    }

}

@Composable
@Preview
fun SongListScreenPreview() {
    SongListScreen(onSongClick = {})
}
