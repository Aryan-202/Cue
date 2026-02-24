package com.music.cue.org.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.music.cue.org.R
import com.music.cue.org.components.SearchBar
import com.music.cue.org.presentation.components.CategoryButton
import com.music.cue.org.presentation.viewmodel.MusicPlayerViewModel
import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueShapes
import com.music.cue.org.theme.CueTheme

data class CategoryItem(
    val id: String,
    val title: String,
    val iconRes: Int,
    val route: String,
    val color: Color
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MusicPlayerViewModel = viewModel()
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val songs by viewModel.songs.collectAsState()
    val albums by viewModel.albums.collectAsState()
    val artists by viewModel.artists.collectAsState()
    val playlists by viewModel.playlists.collectAsState()
    val favoriteSongs by viewModel.favoriteSongs.collectAsState()

    val categories = listOf(
        CategoryItem(
            id = "songs",
            title = "Songs",
            iconRes = R.drawable.ic_launcher_background,
            route = "song_list/all",
            color = CueColors.BluePrimary
        ),
        CategoryItem(
            id = "albums",
            title = "Albums",
            iconRes = R.drawable.ic_launcher_background, // Replace with album icon if available
            route = "album_list",
            color = CueColors.CyanAccent
        ),
        CategoryItem(
            id = "artists",
            title = "Artists",
            iconRes = R.drawable.ic_launcher_background, // Replace with artist icon
            route = "artist_list",
            color = CueColors.TealAccent
        ),
        CategoryItem(
            id = "playlists",
            title = "Playlists",
            iconRes = R.drawable.ic_launcher_background, // Replace with playlist icon
            route = "playlist_list",
            color = CueColors.Success
        ),
        CategoryItem(
            id = "favorites",
            title = "Favorites",
            iconRes = R.drawable.favorite_filled_button,
            route = "favorite_list",
            color = CueColors.FavoriteActive
        ),
        CategoryItem(
            id = "recent",
            title = "Recent",
            iconRes = R.drawable.play_button, // Replace with recent icon
            route = "recent_list",
            color = CueColors.BlueAccent
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar with App Name and Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cue",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = CueColors.BluePrimary
            )

            IconButton(onClick = { /* Navigate to settings */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.more_horizontal_filled_button),
                    contentDescription = "Settings",
                    tint = CueColors.BluePrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Search Bar
        SearchBar(
            onSearch = { query ->
                if (query.isNotBlank()) {
                    navController.navigate("search/$query")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .border(1.dp, CueColors.MediumGray, shape = CueShapes.medium)
                .clip(CueShapes.medium)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(categories) { category ->
                val count = when (category.id) {
                    "songs" -> songs.size
                    "albums" -> albums.size
                    "artists" -> artists.size
                    "playlists" -> playlists.size
                    "favorites" -> favoriteSongs.size
                    "recent" -> viewModel.recentlyPlayed.collectAsState().value.size
                    else -> 0
                }

                CategoryButton(
                    title = category.title,
                    iconRes = category.iconRes,
                    count = count,
                    color = category.color,
                    onClick = {
                        when (category.id) {
                            "songs" -> navController.navigate("song_list/all")
                            "albums" -> navController.navigate("album_list")
                            "artists" -> navController.navigate("artist_list")
                            "playlists" -> navController.navigate("playlist_list")
                            "favorites" -> navController.navigate("favorite_list")
                            "recent" -> navController.navigate("recent_list")
                        }
                    }
                )
            }
        }

        // Mini Player (if a song is playing)
        if (currentSong != null) {
            MiniPlayer(
                song = currentSong!!,
                isPlaying = viewModel.isPlaying.collectAsState().value,
                onPlayPause = { viewModel.playPause() },
                onNext = { viewModel.skipToNext() },
                onPrevious = { viewModel.skipToPrevious() },
                onTap = {
                    navController.navigate("now_playing")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    CueTheme {
        HomeScreen(navController = rememberNavController())
    }
}