package com.music.cue.org

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.music.cue.org.components.PermissionHandler
import com.music.cue.org.presentation.screens.home.HomeScreen
import com.music.cue.org.presentation.viewmodel.PermissionViewModel
import com.music.cue.org.theme.CueTheme
import com.music.cue.org.util.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CueActivity : ComponentActivity() {

    @Inject
    lateinit var permissionManager: PermissionManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissionManager.onPermissionResult(
            permissions.keys.toTypedArray(),
            permissions.values.map { granted ->
                if (granted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
            }.toIntArray()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CueTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val permissionViewModel: PermissionViewModel = viewModel()
                    val permissionState by permissionViewModel.permissionState.collectAsState()
                    var showMainContent by remember { mutableStateOf(false) }

                    // Handle permission state
                    LaunchedEffect(permissionState) {
                        when (permissionState) {
                            is PermissionViewModel.PermissionState.Granted -> {
                                showMainContent = true
                            }
                            else -> {
                                showMainContent = false
                            }
                        }
                    }

                    if (!showMainContent) {
                        PermissionHandler(
                            onPermissionsGranted = {
                                showMainContent = true
                            },
                            permissionManager = permissionManager
                        )
                    } else {
                        MainContent()
                    }
                }
            }
        }
    }

    @Composable
    fun MainContent() {


        androidx.compose.material3.Scaffold(

        ) { paddingValues ->
            HomeScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onSongClick = {
                },
                onGenreClick = { genreId ->
                    // Navigate to genre songs screen
                },
                onSearchClick = {
                    // Navigate to search screen
                }
            )
        }
    }
}