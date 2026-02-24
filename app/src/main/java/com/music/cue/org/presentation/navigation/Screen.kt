package com.music.cue.org.presentation.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class Screen(val route: String) {

    // Screens with NO arguments
    data object Splash : Screen("splash")
    data object Home : Screen("home")
    data object Library : Screen("library")
    data object Search : Screen("search")
    data object Settings : Screen("settings")

    // Screens with arguments using data class
    data class Player(val songId: Long) : Screen("player/${songId}") {
        companion object {
            const val route = "player/{songId}"
            fun createRoute(songId: Long): String = "player/$songId"
        }
    }

    data class Playlist(val playlistId: Long, val playlistName: String) :
        Screen("playlist/${playlistId}?name=${playlistName}") {
        companion object {
            const val route = "playlist/{playlistId}"
            fun createRoute(playlistId: Long, playlistName: String): String =
                "playlist/$playlistId?name=$playlistName"
        }
    }

    data class Album(val albumId: Long, val albumArt: String?) :
        Screen("album/${albumId}") {
        companion object {
            const val route = "album/{albumId}"
            fun createRoute(albumId: Long): String = "album/$albumId"
        }
    }

    // For complex objects - use Parcelable
    @Parcelize
    data class NowPlayingData(
        val songId: Long,
        val songTitle: String,
        val artistName: String,
        val position: Long
    ) : Parcelable

    data class NowPlaying(val data: NowPlayingData) :
        Screen("now_playing/${data.songId}") {
        companion object {
            const val route = "now_playing/{songId}"
            fun createRoute(data: NowPlayingData): String =
                "now_playing/${data.songId}"
        }
    }
}

// Type-safe navigation events
sealed class NavigationEvent {
    data class NavigateTo(val screen: Screen) : NavigationEvent()
    data class NavigateAndPopUpTo(
        val screen: Screen,
        val popUpTo: Screen,
        val inclusive: Boolean = false
    ) : NavigationEvent()
    object PopBackStack : NavigationEvent()
    data class PopUpTo(val screen: Screen, val inclusive: Boolean = false) : NavigationEvent()
}