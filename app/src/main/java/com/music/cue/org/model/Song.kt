package com.music.cue.org.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song (
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val albumArt: Int? = null,
    val isFavorite: Boolean = false
): Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }
}