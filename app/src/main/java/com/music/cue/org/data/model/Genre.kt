package com.music.cue.org.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Genre(
    val id: Long,
    val name: String,
    val songCount: Int,
    val iconRes: Int? = null,
    val colorHex: String = "#FFD700"
) : Parcelable