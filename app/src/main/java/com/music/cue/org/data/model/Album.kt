package com.music.cue.org.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Album(
    val id: Long,
    val name: String,
    val artist: String,
    val songCount: Int,
    val albumArt: Int? = null
) : Parcelable