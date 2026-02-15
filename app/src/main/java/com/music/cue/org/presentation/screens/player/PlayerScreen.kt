package com.music.cue.org.presentation.screens.player

import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.ExoPlayer
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.music.cue.org.R
import java.util.Locale

@Composable
fun PlayerScreen(
    songId: Long,
    onBack: () -> Unit,
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(exoPlayer) {
        viewModel.initializePlayer(exoPlayer)
        onDispose {
            viewModel.releasePlayer()
        }
    }

    LaunchedEffect(uiState.isPlaying) {
        if (uiState.isPlaying) {
            viewModel.updateCurrentPosition()
        } else {
            viewModel.stopPositionUpdate()
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    } else {
        PlayerContent(
            uiState = uiState,
            onBack = onBack,
            onPlayPause = { viewModel.togglePlayPause() },
            onSeek = { position -> viewModel.seekTo(position) },
            getAlbumArtUri = { albumId -> viewModel.getAlbumArtUri(albumId) }
        )
    }
}

@Composable
fun PlayerContent(
    uiState: PlayerUiState,
    onBack: () -> Unit,
    onPlayPause: () -> Unit,
    onSeek: (Long) -> Unit,
    getAlbumArtUri: (Long) -> Uri
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RotationAngle"
    )

    var sliderValue by remember(uiState.currentPosition) {
        mutableFloatStateOf(uiState.currentPosition.toFloat())
    }
    var isDraggingSlider by remember { mutableStateOf(false) }

    uiState.currentSong?.let { song ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xff88DBFF),
                            Color(0xff28A7FF),
                            Color(0xff0F5ABE)
                        )
                    )
                )
        ) {
            // Background blur image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(getAlbumArtUri(song.albumId))
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(18.dp),
                contentScale = ContentScale.Crop,
                alpha = 0.40f,
                error = painterResource(R.drawable.baseline_music_note_24),
                placeholder = painterResource(R.drawable.baseline_music_note_24)
            )

            // Top bar with back button
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 48.dp)
                    .fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0x30ffffff),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0x30ffffff),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = Color.White
                    )
                }
            }

            // Album art and song info
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Album art with rotation animation
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(320.dp)
                ) {
                    // Outer ring
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.1f))
                    )

                    AsyncImage(
                        model = getAlbumArtUri(song.albumId),
                        contentDescription = null,
                        modifier = Modifier
                            .size(280.dp)
                            .graphicsLayer {
                                rotationZ = if (uiState.isPlaying) rotation else 0f
                            }
                            .clip(CircleShape)
                            .background(
                                Color(0xFFEBEBEB),
                                shape = CircleShape
                            ),
                        contentScale = ContentScale.Crop,
                        error = painterResource(R.drawable.baseline_music_note_24),
                        placeholder = painterResource(R.drawable.baseline_music_note_24)
                    )
                }

                Text(
                    text = song.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier
                        .padding(top = 40.dp, start = 24.dp, end = 24.dp)
                        .basicMarquee()
                )

                Text(
                    text = song.artist ?: "Unknown Artist",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Slider and time
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 160.dp)
            ) {
                Slider(
                    value = if (isDraggingSlider) sliderValue else uiState.currentPosition.toFloat(),
                    onValueChange = {
                        isDraggingSlider = true
                        sliderValue = it
                    },
                    onValueChangeFinished = {
                        onSeek(sliderValue.toLong())
                        isDraggingSlider = false
                    },
                    valueRange = 0f..uiState.duration.toFloat().coerceAtLeast(1f),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(
                            if (isDraggingSlider) sliderValue.toLong() else uiState.currentPosition
                        ),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = formatTime(uiState.duration),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }

            // Playback controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 54.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Skip previous (disabled for single song)
                IconButton(
                    onClick = { },
                    enabled = false
                ) {
                    Icon(
                        painterResource(R.drawable.skip_previous_left),
                        contentDescription = "Skip Previous",
                        tint = Color.White.copy(alpha = 0.3f)
                    )
                }

                // Play/Pause
                IconButton(
                    onClick = onPlayPause
                ) {
                    Icon(
                        painterResource(
                            if (uiState.isPlaying) R.drawable.pause_circle
                            else R.drawable.play_circle
                        ),
                        contentDescription = if (uiState.isPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }

                // Skip next (disabled for single song)
                IconButton(
                    onClick = { },
                    enabled = false
                ) {
                    Icon(
                        painterResource(R.drawable.skip_next_right),
                        contentDescription = "Skip Next",
                        tint = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val minutes = (ms / 1000) / 60
    val seconds = (ms / 1000) % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}