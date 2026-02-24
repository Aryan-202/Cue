package com.music.cue.org.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MostPlayedSong(
    val id: Long,
    val title: String,
    val artist: String,
    val playCount: Int,
    val albumArtUri: String? = null,
    val duration: Long
) : Parcelable