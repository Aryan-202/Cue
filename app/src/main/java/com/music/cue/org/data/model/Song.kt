// app/src/main/java/com/music/cue/org/data/model/Song.kt
package com.music.cue.org.data.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val duration: Long,
    val albumId: Long,
    val albumArt: Uri? = null,
    val isFavorite: Boolean = false,
    val playCount: Int = 0,
    val dateAdded: Long = System.currentTimeMillis()
) : Parcelable