package com.music.cue.org.v1

import android.content.ContentUris
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.animation.core.*
import androidx.compose.foundation.basicMarquee
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.music.cue.org.R
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    songList: List<Song>,
    initialIndex: Int = 0,
    onBack: () -> Unit = {},
) {
    val context = LocalContext.current
    var currentIndex by rememberSaveable { mutableIntStateOf(initialIndex) }
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItems = songList.map { MediaItem.fromUri(it.data.toUri()) }
            setMediaItems(mediaItems, if (initialIndex in songList.indices) initialIndex else 0, 0L)
        }
    }

    var isShuffle by rememberSaveable { mutableStateOf(false) }
    var isRepeat by rememberSaveable { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var elapsed by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isRepeat) {
        exoPlayer.repeatMode = if (isRepeat) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
    }

    LaunchedEffect(Unit) {
        exoPlayer.prepare()
        exoPlayer.playWhenReady = true
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlay: Boolean) {
                isPlaying = isPlay
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_READY) {
                    duration = exoPlayer.duration
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)
                currentIndex = exoPlayer.currentMediaItemIndex
            }
        }

        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            elapsed = exoPlayer.currentPosition
            delay(1000)
        }
    }

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

    val currentRotation = remember { Animatable(0f) }
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            // No direct way to resume from same angle easily with infiniteTransition
            // but for beta this is fine or we use a manual Animatable
        }
    }

    var sliderValue by remember(elapsed) { mutableFloatStateOf(elapsed.toFloat()) }
    var isDraggingSlider by remember { mutableStateOf(false) }
    val song = songList.getOrNull(currentIndex)

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
        song?.let { currentSong ->
            val albumUri = ContentUris.withAppendedId(
                "content://media/external/audio/albumart".toUri(),
                currentSong.albumId
            )

            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(albumUri)
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

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 48.dp)
            ) {
                IconButton(
                    onBack,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0x30ffffff),
                            shape = CircleShape
                        )
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Spacer(
                    modifier = Modifier
                        .weight(1f)
                )
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color(0x30ffffff),
                            shape = CircleShape
                        )
                ) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.White)
                }
            }
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                        model = ContentUris.withAppendedId(
                            "content://media/external/audio/albumart".toUri(),
                            currentSong.albumId
                        ),
                        contentDescription = null,
                        modifier = Modifier
                            .size(280.dp)
                            .graphicsLayer {
                                rotationZ = if (isPlaying) rotation else 0f
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
                    text = currentSong.title ?: "Unknown Title",
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
                    text = currentSong.artist ?: "Unknown Artist",
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 160.dp)
            ) {
                Slider(
                    value = if (isDraggingSlider) sliderValue else elapsed.toFloat(),
                    onValueChange = { 
                        isDraggingSlider = true
                        sliderValue = it 
                    },
                    onValueChangeFinished = {
                        exoPlayer.seekTo(sliderValue.toLong())
                        isDraggingSlider = false
                    },
                    valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = formatTime(if (isDraggingSlider) sliderValue.toLong() else elapsed), color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Text(text = formatTime(duration), color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 54.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // repeat
                IconButton(
                    onClick = {
                        isRepeat = !isRepeat
                    },
                ) {
                    Icon(painterResource(R.drawable.repeat_button),
                        contentDescription = "Repeat",
                        tint = if(isRepeat) Color(0xff9c27b0) else Color.White
                    )
                }

                //skip previous
                IconButton(
                    onClick = {
                        exoPlayer.seekToPreviousMediaItem()
                    },

                    ) {
                    Icon(painterResource(R.drawable.skip_previous_left),
                        contentDescription = "Skip Previous",
                        tint = Color.White
                    )
                }

                // play pause
                IconButton(
                    onClick = {
                        if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                    },

                    ) {
                    Icon(painterResource(if (isPlaying) R.drawable.pause_circle else R.drawable.play_circle),
                        contentDescription = "Play/Pause",
                        tint = Color.White
                    )
                }

                // skip next
                IconButton(
                    onClick = {
                        exoPlayer.seekToNextMediaItem()
                    },

                    ) {
                    Icon(painterResource(R.drawable.skip_next_right),
                        contentDescription = "Skip Next",
                        tint = Color.White
                    )
                }

                // shuffle
                IconButton(
                    onClick = {
                        isShuffle = !isShuffle
                        exoPlayer.shuffleModeEnabled = isShuffle
                    },

                    ) {
                    Icon(painterResource(R.drawable.shuffle),
                        contentDescription = "Shuffle",
                        tint = if (isShuffle) Color(0xff9c27b0) else Color.White
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
