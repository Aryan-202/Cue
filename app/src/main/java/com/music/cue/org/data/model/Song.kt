package com.music.cue.org.data.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Song(
    // Core identifiers
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val duration: Long,
    val albumArt: String? = null,

    // App-specific states
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val lastPlayed: Long = 0,
    val skipCount: Int = 0
) : Parcelable {
}