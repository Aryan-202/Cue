package com.music.cue.org.presentation.screens.player

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.music.cue.org.R
import com.music.cue.org.data.model.Song
import com.music.cue.org.data.repository.PlaybackRepository
import com.music.cue.org.presentation.screens.songlist.formatDuration
import com.music.cue.org.presentation.viewmodel.MusicPlayerViewModel
import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    navController: NavController,
    viewModel: MusicPlayerViewModel = viewModel()
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentPosition by viewModel.currentPosition.collectAsState()
    val duration by viewModel.duration.collectAsState()
    val repeatMode by viewModel.repeatMode.collectAsState()
    val shuffleMode by viewModel.shuffleMode.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var showPlaylist by remember { mutableStateOf(false) }
    var volumeLevel by remember { mutableStateOf(0.7f) }

    // Animated rotation for album art
    val rotation = remember { Animatable(0f) }
    val infiniteTransition = rememberInfiniteTransition()

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            rotation.animateTo(
                targetValue = rotation.value + 360,
                animationSpec = infiniteRepeatable(
                    animation = tween(20000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rotation.stop()
        }
    }

    // Pulse animation for equalizer
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Now Playing",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showPlaylist = !showPlaylist }) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Playlist",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            CueColors.BlueDark,
                            Color.Black
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentSong != null) {
                    // Album Art with Rotation
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Rotating disc background
                        Canvas(
                            modifier = Modifier
                                .size(280.dp)
                                .align(Alignment.Center)
                        ) {
                            drawCircle(
                                color = Color.Black.copy(alpha = 0.3f),
                                radius = size.minDimension / 2
                            )
                            for (i in 0..5) {
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.1f),
                                    radius = size.minDimension / 2 - (i * 20f),
                                    style = Stroke(width = 1f)
                                )
                            }
                        }

                        // Album art with rotation
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentSong?.albumArt ?: R.drawable.app_icon)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(if (isPlaying) 280.dp else 240.dp)
                                .clip(CircleShape)
                                .rotate(rotation.value)
                                .background(CueColors.BlueSoft)
                        )

                        // Play/Pause indicator when paused
                        if (!isPlaying) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .padding(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Song Info with Marquee
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = currentSong?.title ?: "Unknown",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Text(
                            text = currentSong?.artist ?: "Unknown Artist",
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Bar with time
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Custom progress bar
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                        ) {
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.3f),
                                size = Size(size.width, 4.dp.toPx()),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                            )

                            val progress = if (duration > 0) {
                                currentPosition.toFloat() / duration.toFloat()
                            } else 0f

                            drawRoundRect(
                                color = CueColors.BlueLight,
                                size = Size(size.width * progress, 4.dp.toPx()),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                            )
                        }

                        // Time labels
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatDuration(currentPosition),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = formatDuration(duration),
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }

                        // Seek bar (invisible touch area)
                        Slider(
                            value = if (duration > 0) currentPosition.toFloat() else 0f,
                            onValueChange = { newPosition ->
                                viewModel.seekTo(newPosition.toLong())
                            },
                            valueRange = 0f..(if (duration > 0) duration.toFloat() else 0f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = Color.Transparent,
                                activeTrackColor = Color.Transparent,
                                inactiveTrackColor = Color.Transparent
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Player Controls
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Shuffle
                        IconButton(
                            onClick = { viewModel.setShuffleMode(!shuffleMode) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = if (shuffleMode) {
                                    painterResource(R.drawable.shuffle)
                                } else {
                                    painterResource(R.drawable.shuffle)
                                },
                                contentDescription = "Shuffle",
                                tint = if (shuffleMode) CueColors.BlueLight else Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Previous
                        IconButton(
                            onClick = { viewModel.skipToPrevious() },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.skip_previous_circle_button),
                                contentDescription = "Previous",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        // Play/Pause
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(CueColors.BlueLight)
                                .scale(if (isPlaying) pulseScale else 1f),
                            contentAlignment = Alignment.Center
                        ) {
                            IconButton(
                                onClick = { viewModel.playPause() },
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    painter = if (isPlaying) {
                                        painterResource(R.drawable.pause_button)
                                    } else {
                                        painterResource(R.drawable.play_button)
                                    },
                                    contentDescription = if (isPlaying) "Pause" else "Play",
                                    tint = Color.Black,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        // Next
                        IconButton(
                            onClick = { viewModel.skipToNext() },
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.skip_next_circle_button),
                                contentDescription = "Next",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                        // Repeat
                        IconButton(
                            onClick = {
                                val newMode = when (repeatMode) {
                                    PlaybackRepository.RepeatMode.NONE -> PlaybackRepository.RepeatMode.ALL
                                    PlaybackRepository.RepeatMode.ALL -> PlaybackRepository.RepeatMode.ONE
                                    PlaybackRepository.RepeatMode.ONE -> PlaybackRepository.RepeatMode.NONE
                                }
                                viewModel.setRepeatMode(newMode)
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                painter = when (repeatMode) {
                                    PlaybackRepository.RepeatMode.NONE -> painterResource(R.drawable.repeat_off_button)
                                    PlaybackRepository.RepeatMode.ALL -> painterResource(R.drawable.repeat_on_button)
                                    PlaybackRepository.RepeatMode.ONE -> painterResource(R.drawable.repeat_one_button)
                                },
                                contentDescription = "Repeat",
                                tint = if (repeatMode != PlaybackRepository.RepeatMode.NONE) {
                                    CueColors.BlueLight
                                } else {
                                    Color.White.copy(alpha = 0.5f)
                                },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Volume control (optional)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (volumeLevel < 0.01f) Icons.Default.VolumeOff else Icons.Default.VolumeDown,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )

                        Slider(
                            value = volumeLevel,
                            onValueChange = { volumeLevel = it },
                            colors = SliderDefaults.colors(
                                thumbColor = CueColors.BlueLight,
                                activeTrackColor = CueColors.BlueLight,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Favorite and More
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        IconButton(onClick = { currentSong?.let { viewModel.toggleFavorite(it) } }) {
                            Icon(
                                imageVector = if (currentSong?.isFavorite == true) {
                                    Icons.Default.Favorite
                                } else {
                                    Icons.Default.FavoriteBorder
                                },
                                contentDescription = "Favorite",
                                tint = if (currentSong?.isFavorite == true) {
                                    CueColors.FavoriteActive
                                } else {
                                    Color.White
                                }
                            )
                        }

                        IconButton(onClick = { /* Add to playlist */ }) {
                            Icon(
                                imageVector = Icons.Default.PlaylistAdd,
                                contentDescription = "Add to playlist",
                                tint = Color.White
                            )
                        }

                        IconButton(onClick = { /* Share */ }) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    // No song playing
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No song playing",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 18.sp
                        )
                    }
                }
            }

            // Playlist drawer (simplified)
            AnimatedVisibility(
                visible = showPlaylist,
                enter = slideInHorizontally(initialOffsetX = { it }),
                exit = slideOutHorizontally(targetOffsetX = { it }),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .align(Alignment.CenterEnd)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Up Next",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // This would show the upcoming songs in playlist
                    // For now, just a placeholder
                    Text(
                        text = "Playlist view coming soon",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}