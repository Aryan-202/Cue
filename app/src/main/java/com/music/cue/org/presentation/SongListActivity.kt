package com.music.cue.org.presentation

import android.content.Intent
import android.os.Bundle
import java.util.ArrayList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.music.cue.org.theme.SongListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SongListScreen { song, position ->
                val intent = Intent(this, PlayerActivity::class.java)
                intent.putParcelableArrayListExtra("songList", ArrayList(song))
                intent.putExtra("position", position)
                startActivity(intent)
            }
        }
    }
}



