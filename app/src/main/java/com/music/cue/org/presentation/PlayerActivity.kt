package com.music.cue.org.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.music.cue.org.v1.Song
import com.music.cue.org.theme.PlayerScreen

class PlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val mySongList = intent.getParcelableArrayListExtra<Song>("songList") ?: emptyList<Song>()
        val initialIndex = intent.getIntExtra("position", 0)
        setContent {
            PlayerScreen(
                songList = mySongList,
                initialIndex = initialIndex,
                onBack = { finish() }
            )
        }
    }
}