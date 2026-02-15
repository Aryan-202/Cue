package com.music.cue.org.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object SongList : Screen("song_list")
    data object Player : Screen("player/{songId}") {
        fun passSongId(songId: Long): String = "player/$songId"
    }
}