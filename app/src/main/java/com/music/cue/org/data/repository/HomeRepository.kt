package com.music.cue.org.data.repository

import com.music.cue.org.data.model.Genre
import com.music.cue.org.data.model.MostPlayedSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeRepository @Inject constructor() {

    // This would normally come from a database or shared preferences
    // For now, we'll return mock data
    fun getMostPlayedSongs(): Flow<List<MostPlayedSong>> = flow {
        emit(
            listOf(
                MostPlayedSong(
                    id = 1L,
                    title = "Bohemian Rhapsody",
                    artist = "Queen",
                    playCount = 157,
                    duration = 354000
                ),
                MostPlayedSong(
                    id = 2L,
                    title = "Shape of You",
                    artist = "Ed Sheeran",
                    playCount = 142,
                    duration = 233000
                ),
                MostPlayedSong(
                    id = 3L,
                    title = "Blinding Lights",
                    artist = "The Weeknd",
                    playCount = 128,
                    duration = 200000
                ),
                MostPlayedSong(
                    id = 4L,
                    title = "Dance Monkey",
                    artist = "Tones and I",
                    playCount = 115,
                    duration = 209000
                ),
                MostPlayedSong(
                    id = 5L,
                    title = "Someone Like You",
                    artist = "Adele",
                    playCount = 98,
                    duration = 285000
                )
            )
        )
    }

    fun getGenres(): Flow<List<Genre>> = flow {
        emit(
            listOf(
                Genre(1L, "Pop", 245, colorHex = "#FF4F9DFF"),
                Genre(2L, "Rock", 189, colorHex = "#FFFF6B6B"),
                Genre(3L, "Hip Hop", 156, colorHex = "#FF845EC2"),
                Genre(4L, "Electronic", 134, colorHex = "#FF00C9A7"),
                Genre(5L, "Jazz", 78, colorHex = "#FFFFD93D"),
                Genre(6L, "Classical", 67, colorHex = "#FF6C5B7B")
            )
        )
    }

    fun getRecentlyPlayed(): Flow<List<MostPlayedSong>> = flow {
        emit(
            listOf(
                MostPlayedSong(
                    id = 6L,
                    title = "Yesterday",
                    artist = "The Beatles",
                    playCount = 45,
                    duration = 125000
                ),
                MostPlayedSong(
                    id = 7L,
                    title = "Rolling in the Deep",
                    artist = "Adele",
                    playCount = 67,
                    duration = 228000
                ),
                MostPlayedSong(
                    id = 8L,
                    title = "Uptown Funk",
                    artist = "Bruno Mars",
                    playCount = 89,
                    duration = 270000
                )
            )
        )
    }

    fun getRecommendedForYou(): Flow<List<MostPlayedSong>> = flow {
        emit(
            listOf(
                MostPlayedSong(
                    id = 9L,
                    title = "Levitating",
                    artist = "Dua Lipa",
                    playCount = 56,
                    duration = 203000
                ),
                MostPlayedSong(
                    id = 10L,
                    title = "Stay",
                    artist = "The Kid LAROI, Justin Bieber",
                    playCount = 78,
                    duration = 141000
                ),
                MostPlayedSong(
                    id = 11L,
                    title = "Bad Habits",
                    artist = "Ed Sheeran",
                    playCount = 92,
                    duration = 230000
                ),
                MostPlayedSong(
                    id = 12L,
                    title = "Montero",
                    artist = "Lil Nas X",
                    playCount = 63,
                    duration = 137000
                )
            )
        )
    }
}