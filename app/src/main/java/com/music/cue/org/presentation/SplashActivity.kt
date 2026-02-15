package com.music.cue.org.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.music.cue.org.theme.SplashScreen

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen (
                onStartClick ={
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            )
        }
    }
}