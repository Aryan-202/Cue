package com.music.cue.org.data

import android.content.Context
import android.provider.MediaStore

fun fetchLocalArtists (context: Context) : MutableList<Artist> {
    val artistList = mutableListOf<Artist>()

    val uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

    val projection = arrayOf(
        MediaStore.Audio.Artists._ID,
        MediaStore.Audio.Artists.ARTIST
    )

    val sortOrder = "${MediaStore.Audio.Artists.ARTIST} ASC"

    val cursor = context.contentResolver.query(
        uri,
        projection,
        null,
        null,
        sortOrder
    )?.use {
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
        val nameColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)

        while (it.moveToNext()) {
            val id = it.getLong(idColumn)
            val name = it.getString(nameColumn)

            if (name != "<unknown>") {
                artistList.add(Artist(id, name))
            }
        }

    }
    return artistList
}