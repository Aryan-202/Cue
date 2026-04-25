package com.music.cue.org.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.music.cue.org.repos.SongRepos

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel(
        factory = HomeScreenViewModelFactory(SongRepos(LocalContext.current))
    ),
    onSongClick: () -> Unit = {}
) {
    val songs by viewModel.songs.collectAsState()
    val selectedSong by viewModel.selectedSong.collectAsState()

    LazyColumn(modifier = modifier.fillMaxSize()) {
        selectedSong?.let { song ->
            item {
                ListItem(
                    headlineContent = { Text("Now Playing: ${song.title}") },
                    supportingContent = { Text(song.artist) },
                    modifier = Modifier.clickable { onSongClick() }
                )
            }
            item {
                HorizontalDivider()
            }
        }
        items(songs) { song ->
            ListItem(
                headlineContent = { Text(song.title) },
                supportingContent = { Text(song.artist) },
                modifier = Modifier.clickable {
                    viewModel.onSongSelected(song)
                    onSongClick()
                }
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview(){
    HomeScreen()
}
