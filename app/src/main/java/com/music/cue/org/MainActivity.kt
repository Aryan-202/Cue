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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.music.cue.org.repos.SongRepos
import com.music.cue.org.screens.home.HomeScreen
import com.music.cue.org.screens.home.HomeScreenViewModel
import com.music.cue.org.screens.home.HomeScreenViewModelFactory
import com.music.cue.org.screens.player.BottomPlayer
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
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (currentRoute == "home" && selectedSong != null) {
                            val currentPosition by homeScreenViewModel.currentPosition.collectAsState()
                            val duration by homeScreenViewModel.duration.collectAsState()
                            BottomPlayer(
                                song = selectedSong,
                                isPlaying = isPlaying,
                                currentPosition = currentPosition,
                                duration = duration,
                                onTogglePlay = { homeScreenViewModel.togglePlayPause() },
                                onSkipPrevious = { homeScreenViewModel.previous() },
                                onSkipNext = { homeScreenViewModel.next() },
                                onSwipeUp = { navController.navigate("player") }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") {
                            HomeScreen(
                                viewModel = homeScreenViewModel,
                                onSongClick = {
                                    navController.navigate("player")
                                }
                            )
                        }
                        composable("player") {
                            val currentPosition by homeScreenViewModel.currentPosition.collectAsState()
                            val duration by homeScreenViewModel.duration.collectAsState()
                            PlayerScreen(
                                song = selectedSong,
                                isPlaying = isPlaying,
                                currentPosition = currentPosition,
                                duration = duration,
                                onTogglePlay = { homeScreenViewModel.togglePlayPause() },
                                onNext = { homeScreenViewModel.next() },
                                onPrevious = { homeScreenViewModel.previous() },
                                onSeek = { homeScreenViewModel.seekTo(it) },
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}

