package com.music.cue.org.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.music.cue.org.presentation.screens.player.PlayerScreen
import com.music.cue.org.presentation.screens.songlist.SongListScreen
import com.music.cue.org.presentation.screens.splash.SplashScreen
import com.music.cue.org.presentation.screens.splash.SplashViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Splash.route) {
            val viewModel: SplashViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            SplashScreen(
                uiState = uiState,
                onTimeout = {
                    navController.navigate(Screen.SongList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.SongList.route) {
            SongListScreen(
                onSongClick = { song ->
                    navController.navigate(Screen.Player.passSongId(song.id))
                }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(
                navArgument("songId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val songId = backStackEntry.arguments?.getLong("songId") ?: 0L
            PlayerScreen(
                songId = songId,
                onBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}