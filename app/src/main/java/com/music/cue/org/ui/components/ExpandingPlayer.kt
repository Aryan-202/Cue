package com.music.cue.org.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.music.cue.org.data.Song
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandingPlayer(
    song: Song?,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (song == null) return

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    
    val collapsedHeight = 64.dp
    val collapsedHeightPx = with(density) { collapsedHeight.toPx() }
    val bottomMargin = 8.dp
    val bottomMarginPx = with(density) { bottomMargin.toPx() }

    var containerHeightPx by remember { mutableFloatStateOf(0f) }
    
    // Calculate the resting position (swipeLimit) once we know the container height
    val swipeLimit = remember(containerHeightPx) { 
        (containerHeightPx - collapsedHeightPx - bottomMarginPx).coerceAtLeast(0f)
    }

    // Use Animatable for smooth, interruptible animation and natural dragging
    val offset = remember { Animatable(0f) }
    var isExpanded by remember { mutableStateOf(false) }
    var isOffsetInitialized by remember { mutableStateOf(false) }

    // Initialize offset to swipeLimit when swipeLimit is first known
    LaunchedEffect(swipeLimit) {
        if (swipeLimit > 0 && !isOffsetInitialized) {
            if (!isExpanded) offset.snapTo(swipeLimit)
            isOffsetInitialized = true
        }
    }

    // Automatically expand when a new song starts playing
    LaunchedEffect(song.id) {
        if (!isExpanded && isOffsetInitialized) {
            isExpanded = true
            offset.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
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
            .onGloballyPositioned { containerHeightPx = it.size.height.toFloat() }
    ) {
        if (containerHeightPx > 0) {
            Surface(
                modifier = Modifier
                    .graphicsLayer { translationY = currentOffset }
                    .fillMaxWidth()
                    .padding(horizontal = lerp(6f, 0f, fraction).dp)
                    // The height increases as it moves up, but stays floating when collapsed
                    .height(with(density) { 
                        lerp(collapsedHeightPx, containerHeightPx, fraction).toDp() 
                    })
                    .draggable(
                        orientation = Orientation.Vertical,
                        state = rememberDraggableState { delta ->
                            coroutineScope.launch {
                                offset.snapTo((offset.value + delta).coerceIn(0f, swipeLimit))
                            }
                        },
                        onDragStopped = { velocity ->
                            val targetValue = if (isExpanded) {
                                if (velocity > 500 || offset.value > 20f) swipeLimit else 0f
                            } else {
                                if (velocity < -500 || offset.value < swipeLimit - 20f) 0f else swipeLimit
                            }
                            isExpanded = targetValue == 0f
                            coroutineScope.launch {
                                offset.animateTo(targetValue, spring(stiffness = Spring.StiffnessMediumLow))
                            }
                        }
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isExpanded = !isExpanded
                        coroutineScope.launch {
                            offset.animateTo(
                                if (isExpanded) 0f else swipeLimit,
                                spring(stiffness = Spring.StiffnessMediumLow)
                            )
                        }
                    },
                shape = RoundedCornerShape(lerp(12f, 0f, fraction).dp),
                color = if (fraction < 0.1f) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Image Transition (Diagonal)
                    val imageSize = lerp(48f, 300f, fraction).dp
                    val imageCorner = lerp(8f, 16f, fraction).dp
                    
                    val miniX = with(density) { 8.dp.toPx() }
                    val miniY = with(density) { 8.dp.toPx() }
                    val fullX = with(density) { (configuration.screenWidthDp.dp / 2 - imageSize / 2).toPx() }
                    val fullY = with(density) { 100.dp.toPx() }

                    val currentImageX = lerp(miniX, fullX, fraction)
                    val currentImageY = lerp(miniY, fullY, fraction)

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(song.albumArtUri)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .offset { IntOffset(currentImageX.roundToInt(), currentImageY.roundToInt()) }
                            .size(imageSize)
                            .clip(RoundedCornerShape(imageCorner)),
                        contentScale = ContentScale.Crop
                    )

                    // Mini Controls
                    if (fraction < 0.8f) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(collapsedHeight)
                                .alpha(1f - (fraction / 0.8f))
                                .padding(start = 60.dp, end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                MarqueeText(
                                    text = song.title,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = song.artist,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
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
                                .alpha((fraction - 0.2f) / 0.8f)
                                .padding(top = 100.dp + imageSize + 32.dp, start = 24.dp, end = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            MarqueeText(
                                text = song.title,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Text(
                                text = song.artist,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Spacer(modifier = Modifier.height(48.dp))
                            
                            CustomLinearProgressIndicator(
                                progress = if (duration > 0) currentPosition.toFloat() / duration else 0f,
                                modifier = Modifier.fillMaxWidth().height(12.dp)
                            )
                            
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
                        
                        TopAppBar(
                            title = { Text("Now Playing", style = MaterialTheme.typography.titleMedium) },
                            navigationIcon = {
                                IconButton(onClick = { 
                                    isExpanded = false
                                    coroutineScope.launch {
                                        offset.animateTo(swipeLimit, spring(stiffness = Spring.StiffnessMediumLow))
                                    }
                                }) {
                                    Icon(Icons.Default.KeyboardArrowDown, null)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                            modifier = Modifier.alpha((fraction - 0.5f) / 0.5f)
                        )
                    }
                    
                    // Bottom progress (collapsed)
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

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}
