package com.music.cue.org.data.model


import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.music.cue.org.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val albumId: Long,
    val uri: String,
    val albumArtUri: String?,
    val dateAdded: Long,
    val isFavorite: Boolean = false
) : Parcelable