package com.music.cue.org.data
import com.music.cue.org.R
import com.music.cue.org.model.Song

object FavSongDummyData {
    val favoriteSongs = listOf(
        Song(
            id = 1,
            title = "Bohemian Rhapsody",
            artist = "Queen",
            duration = 354000, // 5:54 in milliseconds
            albumArt = R.drawable.ic_launcher_background,
            isFavorite = true
        ),
        Song(
            id = 2,
            title = "Shape of You",
            artist = "Ed Sheeran",
            duration = 233000, // 3:53
            albumArt = R.drawable.ic_launcher_background,
            isFavorite = true
        ),
        Song(
            id = 3,
            title = "Blinding Lights",
            artist = "The Weeknd",
            duration = 200000, // 3:20
            albumArt = R.drawable.ic_launcher_background,
            isFavorite = true
        ),
        Song(
            id = 4,
            title = "Someone Like You",
            artist = "Adele",
            duration = 285000, // 4:45
            albumArt = R.drawable.ic_launcher_background,
            isFavorite = true
        ),
        Song(
            id = 5,
            title = "Hotel California",
            artist = "Eagles",
            duration = 391000, // 6:31
            albumArt = R.drawable.ic_launcher_background,
            isFavorite = true
        ),
        Song(
            id = 6,
            title = "Imagine",
            artist = "John Lennon",
            duration = 183000, // 3:03
            albumArt = R.drawable.ic_launcher_background,
            isFavorite = true
        ),
        Song(
            id = 7,
            title = "Smells Like Teen Spirit",
            artist = "Nirvana",
            duration = 301000, // 5:01
            albumArt = R.drawable.ic_launcher_background,
            isFavorite = true
        ),
        Song(
            id = 8,
            title = "Sweet Child O' Mine",
            artist = "Guns N' Roses",
            duration = 355000, // 5:55
            albumArt = R.drawable.ic_launcher_background,
            isFavorite = true
        )
    )
}