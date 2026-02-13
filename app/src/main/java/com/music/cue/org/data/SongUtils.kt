package com.music.cue.org.data

import android.content.Context
import android.provider.MediaStore

fun getSongs(context: Context): List<Song> {
    val songs = mutableListOf<Song>()
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
    val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

    val projection = arrayOf(
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.DATA,
        MediaStore.Audio.Media.ALBUM_ID
    )

    val cursor = context.contentResolver.query(
        uri,
        projection,
        selection,
        null,
        sortOrder
    )

    cursor?.use{
        val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
        val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
        val dataColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
        val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

        while (it.moveToNext()){
            val id = it.getLong(idColumn)
            val title = it.getString(titleColumn)
            val artist = it.getString(artistColumn)
            val data = it.getString(dataColumn)
            val albumId = it.getLong(albumIdColumn)
            songs.add(Song(id, title, artist, data, albumId))
        }
    }
    return songs
}