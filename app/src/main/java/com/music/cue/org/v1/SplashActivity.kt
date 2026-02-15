package com.music.cue.org.v1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

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