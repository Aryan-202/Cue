package com.music.cue.org

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.music.cue.org.presentation.screens.splash.SplashScreen
import com.music.cue.org.theme.CueTheme

class CueActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CueTheme {
                // State to track if splash is showing
                var isSplashShowing by remember { mutableStateOf(true) }

                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isSplashShowing) {
                        SplashScreen(
                            onSplashFinished = {
                                isSplashShowing = false
                            }
                        )
                    } else {
                        // We'll replace this with HomeScreen later
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Home Screen (Coming Soon)")
                        }
                    }
                }
            }
        }
    }
}