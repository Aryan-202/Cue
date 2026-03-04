package com.music.cue.org

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.music.cue.org.presentation.screens.songlist.SongListScreen
import com.music.cue.org.presentation.screens.home.HomeScreen
import com.music.cue.org.presentation.viewmodel.MusicPlayerViewModel
import com.music.cue.org.theme.CueTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CueActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CueTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CueApp()
                }
            }
        }
    }
}

@Composable
fun CueApp() {
    val navController = rememberNavController()
    val viewModel: MusicPlayerViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable("song_list/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "all"
            val songs = when (type) {
                "all" -> viewModel.songs.collectAsState().value
                "favorites" -> viewModel.favoriteSongs.collectAsState().value
                "recent" -> viewModel.recentlyPlayed.collectAsState().value
                else -> viewModel.songs.collectAsState().value
            }
            val title = when (type) {
                "all" -> "All Songs"
                "favorites" -> "Favorites"
                "recent" -> "Recently Played"
                else -> "Songs"
            }
            SongListScreen(
                navController = navController,
                title = title,
                songs = songs,
                viewModel = viewModel
            )
        }

        composable("album_list") {
            // TODO: Implement AlbumListScreen
        }

        composable("artist_list") {
            // TODO: Implement ArtistListScreen
        }

        composable("playlist_list") {
            // TODO: Implement PlaylistListScreen
        }

        composable("favorite_list") {
            SongListScreen(
                navController = navController,
                title = "Favorites",
                songs = viewModel.favoriteSongs.collectAsState().value,
                viewModel = viewModel
            )
        }

        composable("recent_list") {
            SongListScreen(
                navController = navController,
                title = "Recently Played",
                songs = viewModel.recentlyPlayed.collectAsState().value,
                viewModel = viewModel
            )
        }

        composable("search/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            val allSongs = viewModel.songs.collectAsState().value
            val searchResults = allSongs.filter { song ->
                song.title.contains(query, ignoreCase = true) ||
                        song.artist.contains(query, ignoreCase = true)
            }
            SongListScreen(
                navController = navController,
                title = "Search: $query",
                songs = searchResults,
                viewModel = viewModel
            )
        }

        composable("now_playing") {
            // TODO: Implement NowPlayingScreen
        }
    }
}