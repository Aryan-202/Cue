package com.music.cue.org.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class UserPrefs(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("cue_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_LAST_SONG_ID = "last_song_id"
        private const val KEY_LAST_POSITION = "last_position"
    }

    fun saveLastSongId(id: Long) {
        prefs.edit { putLong(KEY_LAST_SONG_ID, id) }
    }

    fun getLastSongId(): Long {
        return prefs.getLong(KEY_LAST_SONG_ID, -1L)
    }

    fun saveLastPosition(position: Long) {
        prefs.edit { putLong(KEY_LAST_POSITION, position) }
    }

    fun getLastPosition(): Long {
        return prefs.getLong(KEY_LAST_POSITION, 0L)
    }
}
