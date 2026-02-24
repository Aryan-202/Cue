package com.music.cue.org.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.music.cue.org.presentation.screens.splash.SplashScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash::class,
        modifier = modifier
    ) {
        // Splash Screen (no args)
        composable<Screen.Splash> {
            SplashScreen(
                onSplashFinished = {
                    navController.navigateTo(Screen.Home)
                }
            )
        }

        // Home Screen (no args)


        // Library Screen (no args)


        // Search Screen (no args)


        // Settings Screen (no args)


        // Player Screen with arguments


        // Playlist Screen with multiple arguments


        // Album Screen with optional arguments


        // Now Playing with complex data

    }
}

// Extension functions for type-safe navigation
fun NavHostController.navigateTo(
    screen: Screen,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    when (screen) {
        is Screen.Splash -> navigate(Screen.Splash::class, builder)
        is Screen.Home -> navigate(Screen.Home::class, builder)
        is Screen.Library -> navigate(Screen.Library::class, builder)
        is Screen.Search -> navigate(Screen.Search::class, builder)
        is Screen.Settings -> navigate(Screen.Settings::class, builder)
        is Screen.Player -> navigate(Screen.Player(screen.songId), builder)
        is Screen.Playlist -> navigate(
            Screen.Playlist(screen.playlistId, screen.playlistName),
            builder
        )
        is Screen.Album -> navigate(
            Screen.Album(screen.albumId, screen.albumArt),
            builder
        )
        is Screen.NowPlaying -> navigate(
            Screen.NowPlaying(screen.data),
            builder
        )
    }
}

// Helper to get current screen
@Composable
fun NavHostController.currentScreenAsState(): Screen? {
    val backStackEntry by currentBackStackEntryAsState()
    return backStackEntry?.toScreen()
}

fun NavBackStackEntry.toScreen(): Screen? {
    return when (this.destination.route) {
        Screen.Splash::class.qualifiedName -> Screen.Splash
        Screen.Home::class.qualifiedName -> Screen.Home
        Screen.Library::class.qualifiedName -> Screen.Library
        Screen.Search::class.qualifiedName -> Screen.Search
        Screen.Settings::class.qualifiedName -> Screen.Settings
        Screen.Player.route -> this.toRoute<Screen.Player>()
        Screen.Playlist.route -> this.toRoute<Screen.Playlist>()
        Screen.Album.route -> this.toRoute<Screen.Album>()
        Screen.NowPlaying.route -> this.toRoute<Screen.NowPlaying>()
        else -> null
    }
}