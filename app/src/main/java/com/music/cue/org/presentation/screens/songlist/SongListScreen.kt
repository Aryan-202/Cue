package com.music.cue.org.presentation.screens.songlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.music.cue.org.R
import com.music.cue.org.data.model.Song
import com.music.cue.org.presentation.components.BottomPlayer
import com.music.cue.org.presentation.viewmodel.MusicPlayerViewModel
import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueTheme
import com.music.cue.org.util.DurationFormatter.formatDuration

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListScreenContent(
    navController: NavController,
    title: String,
    songs: List<Song>,
    currentSong: Song?,
    isPlaying: Boolean,
    onSongClick: (Song) -> Unit,
    onFavoriteClick: (Song) -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CueColors.BluePrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Song List - Takes remaining space
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(paddingValues)
            ) {
                if (songs.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No songs found",
                            fontSize = 16.sp,
                            color = CueColors.MediumGray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(songs) { song ->
                            SongListItem(
                                song = song,
                                isPlaying = currentSong?.id == song.id && isPlaying,
                                onClick = { onSongClick(song) },
                                onFavoriteClick = { onFavoriteClick(song) }
                            )
                        }
                    }
                }
            }

            // Bottom Player - Always visible at bottom
            if (currentSong != null) {
                BottomPlayer(
                    song = currentSong,
                    isPlaying = isPlaying,
                    onPlayPauseClick = onPlayPauseClick,
                    onNextClick = onNextClick,
                    onPreviousClick = onPreviousClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SongListScreen(
    navController: NavController,
    title: String = "All Songs",
    songs: List<Song>,
    viewModel: MusicPlayerViewModel = viewModel()
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    SongListScreenContent(
        navController = navController,
        title = title,
        songs = songs,
        currentSong = currentSong,
        isPlaying = isPlaying,
        onSongClick = { song ->
            if (currentSong?.id == song.id) {
                viewModel.playPause()
            } else {
                viewModel.playSongs(songs, songs.indexOf(song))
            }
        },
        onFavoriteClick = { song -> viewModel.toggleFavorite(song) },
        onPlayPauseClick = { viewModel.playPause() },
        onNextClick = { viewModel.playNext() },
        onPreviousClick = { viewModel.playPrevious() }
    )
}

@Composable
fun SongListItem(
    song: Song,
    isPlaying: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album Art
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(CueColors.BlueSoft)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.albumArtUri ?: R.drawable.ic_launcher_background)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )

            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Song Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = song.title,
                color = if (isPlaying) CueColors.BluePrimary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
                fontSize = 16.sp,
                maxLines = 1
            )

            Text(
                text = song.artist,
                color = CueColors.MediumGray,
                fontSize = 14.sp,
                maxLines = 1
            )
        }

        // Duration
        Text(
            text = formatDuration(song.duration),
            color = CueColors.MediumGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(end = 8.dp)
        )

        // Favorite Button
        IconButton(
            onClick = onFavoriteClick,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                painter = if (song.isFavorite)
                    painterResource(R.drawable.favorite_filled_button)
                else
                    painterResource(R.drawable.favorite_empty_button),
                contentDescription = "Favorite",
                tint = if (song.isFavorite) CueColors.FavoriteActive else CueColors.MediumGray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun SongListScreenPreview() {
    CueTheme {
        SongListScreenContent(
            navController = rememberNavController(),
            title = "All Songs",
            songs = sampleSongs,
            currentSong = sampleSongs[0],
            isPlaying = true,
            onSongClick = {},
            onFavoriteClick = {},
            onPlayPauseClick = {},
            onNextClick = {},
            onPreviousClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SongListScreenEmptyPreview() {
    CueTheme {
        SongListScreenContent(
            navController = rememberNavController(),
            title = "Favorites",
            songs = emptyList(),
            currentSong = null,
            isPlaying = false,
            onSongClick = {},
            onFavoriteClick = {},
            onPlayPauseClick = {},
            onNextClick = {},
            onPreviousClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SongListItemPreview() {
    CueTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SongListItem(
                song = sampleSongs[0],
                isPlaying = false,
                onClick = {},
                onFavoriteClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongListItemPlayingPreview() {
    CueTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SongListItem(
                song = sampleSongs[0].copy(isFavorite = true),
                isPlaying = true,
                onClick = {},
                onFavoriteClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongListItemFavoritePreview() {
    CueTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SongListItem(
                song = sampleSongs[0].copy(isFavorite = true),
                isPlaying = false,
                onClick = {},
                onFavoriteClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongListItemLongTextPreview() {
    CueTheme {
        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
            SongListItem(
                song = Song(
                    id = 1,
                    title = "This is a very long song title that should be truncated properly",
                    artist = "This is a very long artist name that should also be truncated",
                    album = "Album Name",
                    duration = 245000,
                    albumId = 1,
                    uri = "content://media/external/audio/media/1",
                    albumArtUri = null,
                    dateAdded = 1624556800000,
                    isFavorite = false
                ),
                isPlaying = false,
                onClick = {},
                onFavoriteClick = {}
            )
        }
    }
}

// Sample data for previews
private val sampleSongs = listOf(
    Song(
        id = 1,
        title = "Bohemian Rhapsody",
        artist = "Queen",
        album = "A Night at the Opera",
        duration = 354000,
        albumId = 1,
        uri = "content://media/external/audio/media/1",
        albumArtUri = null,
        dateAdded = 1624556800000,
        isFavorite = false
    ),
    Song(
        id = 2,
        title = "Imagine",
        artist = "John Lennon",
        album = "Imagine",
        duration = 183000,
        albumId = 2,
        uri = "content://media/external/audio/media/2",
        albumArtUri = null,
        dateAdded = 1624556800001,
        isFavorite = true
    ),
    Song(
        id = 3,
        title = "Hotel California",
        artist = "Eagles",
        album = "Hotel California",
        duration = 391000,
        albumId = 3,
        uri = "content://media/external/audio/media/3",
        albumArtUri = null,
        dateAdded = 1624556800002,
        isFavorite = false
    )
)
