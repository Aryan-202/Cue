package com.music.cue.org

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.music.cue.org.components.ArtistsGrid
import com.music.cue.org.components.CueNavigation
import com.music.cue.org.components.CueTopBar
import com.music.cue.org.theme.CueTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CueActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This allows your app to draw behind the system status bar and navigation bar
        enableEdgeToEdge()

        setContent {
            CueTheme {
                var searchQuery by rememberSaveable { mutableStateOf("") }

                // The Scaffold organizes your top bar, bottom bar, and content
                Scaffold(
                    modifier = Modifier.fillMaxSize(),

                    // 1. Slot your search bar at the top
                    topBar = {
                        CueTopBar(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it }
                        )
                    },

                    // 2. Slot your navigation at the bottom
                    bottomBar = {
                        CueNavigation()
                    }

                ) { innerPadding ->
                    // The main content of the screen
                    ArtistsGrid(
                        searchQuery = searchQuery,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CueTheme {
        // You can add a preview here if needed
    }
}
