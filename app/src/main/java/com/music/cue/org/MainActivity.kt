package com.music.cue.org

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.music.cue.org.repos.SongRepos
import com.music.cue.org.screens.home.HomeScreen
import com.music.cue.org.screens.home.HomeScreenViewModel
import com.music.cue.org.screens.home.HomeScreenViewModelFactory
import com.music.cue.org.screens.player.PlayerScreen
import com.music.cue.org.screens.splash.SplashScreenViewModel
import com.music.cue.org.ui.theme.CueTheme
import com.music.cue.org.utils.Permissions

class MainActivity : ComponentActivity() {

    private val splashScreenViewModel: SplashScreenViewModel by viewModels()
    private val homeScreenViewModel: HomeScreenViewModel by viewModels {
        HomeScreenViewModelFactory(SongRepos(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            splashScreenViewModel.isSplashScreenVisible.value
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                homeScreenViewModel.refreshSongs()
            }
        }

        if (!Permissions.hasStoragePermission(this)) {
            requestPermissionLauncher.launch(Permissions.getStoragePermission())
        }

        homeScreenViewModel.initController(this)

        setContent {
            CueTheme {
                val navController = rememberNavController()
                val selectedSong by homeScreenViewModel.selectedSong.collectAsState()
                val isPlaying by homeScreenViewModel.isPlaying.collectAsState()
                
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                viewModel = homeScreenViewModel,
                                onSongClick = {
                                    navController.navigate("player")
                                }
                            )
                        }
                    }
                    composable("player") {
                        PlayerScreen(
                            song = selectedSong,
                            isPlaying = isPlaying,
                            onTogglePlay = { homeScreenViewModel.togglePlayPause() },
                            onNext = { homeScreenViewModel.next() },
                            onPrevious = { homeScreenViewModel.previous() },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

