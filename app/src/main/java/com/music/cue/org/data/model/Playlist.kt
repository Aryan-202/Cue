package com.music.cue.org.model

import android.os.Parcelable
import com.music.cue.org.data.model.Song
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val id: Long,
    val name: String,
    val songCount: Int,
    val playlistArt: Int? = null,
    val songs: List<Song> = emptyList()
) : Parcelable