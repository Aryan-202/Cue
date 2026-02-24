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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.music.cue.org.presentation.viewmodel.MusicPlayerViewModel

import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueIcons
import com.music.cue.org.theme.CueTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListScreen(
    navController: NavController,
    title: String = "All Songs",
    songs: List<Song>,
    viewModel: MusicPlayerViewModel = viewModel()
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

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
                            painter = painterResource(R.drawable.ic_launcher_background),
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
                            onClick = {
                                if (currentSong?.id == song.id) {
                                    viewModel.playPause()
                                } else {
                                    viewModel.playSongs(songs, songs.indexOf(song))
                                }
                            },
                            onFavoriteClick = {
                                viewModel.toggleFavorite(song)
                            }
                        )
                    }
                }
            }
        }
    }
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
                .clip(RoundedCornerShape(8.dp))
                .background(CueColors.BlueSoft)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.albumArt ?: R.drawable.ic_launcher_background)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(CueIcons.Play),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
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

fun formatDuration(duration: Long): String {
    val seconds = (duration / 1000) % 60
    val minutes = (duration / (1000 * 60)) % 60
    val hours = (duration / (1000 * 60 * 60))
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

@Preview
@Composable
fun SongListScreenPreview() {
    CueTheme {
        SongListScreen(
            navController = rememberNavController(),
            title = "All Songs",
            songs = listOf(
                Song(1, "Test Song 1", "Artist 1", 180000, 1, ),
                Song(2, "Test Song 2", "Artist 2", 240000, 1, ),
                Song(3, "Test Song 3", "Artist 3", 300000, 1, )
            )
        )
    }
}