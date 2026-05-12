package com.music.cue.org.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.music.cue.org.data.Song
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandingPlayer(
    song: Song?,
    songs: List<Song>,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onSongSelected: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    if (song == null) return

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    val collapsedHeight = 64.dp
    val collapsedHeightPx = with(density) { collapsedHeight.toPx() }
    val bottomMargin = 12.dp
    val bottomMarginPx = with(density) { bottomMargin.toPx() }

    var containerHeightPx by remember { mutableFloatStateOf(0f) }
    
    val swipeLimit = remember(containerHeightPx) { 
        (containerHeightPx - collapsedHeightPx - bottomMarginPx).coerceAtLeast(0f)
    }

    val offset = remember { Animatable(2000f) }
    var isExpanded by remember { mutableStateOf(false) }
    var isOffsetInitialized by remember { mutableStateOf(false) }

    // Initialize Pager State
    val initialPage = remember(song.id) { songs.indexOfFirst { it.id == song.id }.coerceAtLeast(0) }
    val pagerState = rememberPagerState(initialPage = initialPage) { songs.size }

    // Sync Pager with External Song Changes
    LaunchedEffect(song.id) {
        val index = songs.indexOfFirst { it.id == song.id }
        if (index >= 0 && index != pagerState.currentPage) {
            pagerState.scrollToPage(index)
        }
    }

    // Sync External Song with Pager Changes
    LaunchedEffect(pagerState.currentPage) {
        if (songs.isNotEmpty() && songs[pagerState.currentPage].id != song.id) {
            onSongSelected(songs[pagerState.currentPage])
        }
    }

    LaunchedEffect(swipeLimit) {
        if (swipeLimit > 0 && !isOffsetInitialized) {
            isOffsetInitialized = true
            offset.animateTo(swipeLimit, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
        }
    }

    val currentOffset = offset.value
    val fraction = if (swipeLimit > 0) (1f - (currentOffset / swipeLimit)).coerceIn(0f, 1f) else 1f

    BackHandler(enabled = isExpanded) {
        isExpanded = false
        coroutineScope.launch {
            offset.animateTo(swipeLimit, spring(stiffness = Spring.StiffnessMediumLow))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .onGloballyPositioned { 
                val height = it.size.height.toFloat()
                if (containerHeightPx != height) containerHeightPx = height 
            }
    ) {
        if (containerHeightPx > 0) {
            Surface(
                modifier = Modifier
                    .graphicsLayer { translationY = currentOffset }
                    .fillMaxWidth()
                    .padding(horizontal = lerp(12f, 0f, fraction).dp)
                    .height(with(density) { lerp(collapsedHeightPx, containerHeightPx, fraction).toDp() })
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            coroutineScope.launch { offset.snapTo((offset.value + delta).coerceIn(0f, swipeLimit)) }
                        },
                        onDragStopped = { velocity ->
                            val targetValue = if (isExpanded) {
                                if (velocity > 500 || offset.value > 20f) swipeLimit else 0f
                            } else {
                                if (velocity < -500 || offset.value < swipeLimit - 20f) 0f else swipeLimit
                            }
                            isExpanded = targetValue == 0f
                            coroutineScope.launch { offset.animateTo(targetValue, spring(stiffness = Spring.StiffnessMediumLow)) }
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isExpanded = !isExpanded
                        coroutineScope.launch {
                            offset.animateTo(if (isExpanded) 0f else swipeLimit, spring(stiffness = Spring.StiffnessMediumLow))
                        }
                    },
                shape = RoundedCornerShape(lerp(12f, 0f, fraction).dp),
                color = if (fraction < 0.1f) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = !isExpanded, // Only swipe when collapsed
                        beyondViewportPageCount = 1
                    ) { pageIndex ->
                        val currentSong = songs[pageIndex]
                        PlayerPageContent(
                            song = currentSong,
                            fraction = fraction,
                            isPlaying = isPlaying && pageIndex == pagerState.currentPage,
                            currentPosition = currentPosition,
                            duration = duration,
                            onTogglePlay = onTogglePlay,
                            onNext = onNext,
                            onPrevious = onPrevious,
                            onSeek = onSeek,
                            configuration = configuration,
                            density = density
                        )
                    }

                    // Top Bar (Static, doesn't swipe)
                    if (fraction > 0.2f) {
                        TopAppBar(
                            title = { Text("Now Playing", style = MaterialTheme.typography.titleMedium) },
                            navigationIcon = {
                                IconButton(onClick = { 
                                    isExpanded = false
                                    coroutineScope.launch { offset.animateTo(swipeLimit, spring(stiffness = Spring.StiffnessMediumLow)) }
                                }) {
                                    Icon(Icons.Default.KeyboardArrowDown, null)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                            modifier = Modifier.graphicsLayer { alpha = ((fraction - 0.5f) / 0.5f).coerceIn(0f, 1f) }
                        )
                    }
                    
                    // Bottom progress (collapsed, static)
                    if (fraction < 0.05f) {
                        CustomLinearProgressIndicator(
                            progress = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .height(2.dp),
                            clipShape = RoundedCornerShape(0.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerPageContent(
    song: Song,
    fraction: Float,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    configuration: android.content.res.Configuration,
    density: androidx.compose.ui.unit.Density
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Image Transition
        val baseImageSize = 300.dp
        val miniImageSize = 48.dp
        
        val miniX = with(density) { 8.dp.toPx() }
        val miniY = with(density) { 8.dp.toPx() }
        val fullX = with(density) { (configuration.screenWidthDp.dp / 2 - baseImageSize / 2).toPx() }
        val fullY = with(density) { 100.dp.toPx() }

        val currentImageX = lerp(miniX, fullX, fraction)
        val currentImageY = lerp(miniY, fullY, fraction)

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(song.albumArtUri).crossfade(true).build(),
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(currentImageX.roundToInt(), currentImageY.roundToInt()) }
                .size(lerp(miniImageSize.value, baseImageSize.value, fraction).dp)
                .clip(RoundedCornerShape(lerp(8f, 16f, fraction).dp)),
            contentScale = ContentScale.Crop
        )

        // Mini Controls
        if (fraction < 0.8f) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .graphicsLayer { alpha = (1f - (fraction / 0.8f)).coerceIn(0f, 1f) }
                    .padding(start = 64.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    MarqueeText(text = song.title, style = MaterialTheme.typography.bodyLarge)
                    Text(text = song.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = onPrevious) {
                    Icon(Icons.Default.SkipPrevious, null)
                }
                IconButton(onClick = onTogglePlay) {
                    Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null)
                }
                IconButton(onClick = onNext) {
                    Icon(Icons.Default.SkipNext, null)
                }
            }
        }

        // Full Controls
        if (fraction > 0.2f) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { alpha = ((fraction - 0.2f) / 0.8f).coerceIn(0f, 1f) }
                    .padding(top = 100.dp + baseImageSize + 32.dp, start = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MarqueeText(text = song.title, style = MaterialTheme.typography.headlineMedium)
                Text(text = song.artist, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(modifier = Modifier.height(48.dp))

                Slider(
                    value = if (duration > 0) currentPosition.toFloat() else 0f,
                    onValueChange = { onSeek(it.toLong()) },
                    valueRange = 0f..(duration.toFloat().coerceAtLeast(1f)),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = formatTime(currentPosition), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = formatTime(duration), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onPrevious, modifier = Modifier.size(64.dp)) {
                        Icon(Icons.Default.SkipPrevious, null, modifier = Modifier.size(40.dp))
                    }
                    FilledIconButton(onClick = onTogglePlay, modifier = Modifier.size(80.dp)) {
                        Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, modifier = Modifier.size(48.dp))
                    }
                    IconButton(onClick = onNext, modifier = Modifier.size(64.dp)) {
                        Icon(Icons.Default.SkipNext, null, modifier = Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
