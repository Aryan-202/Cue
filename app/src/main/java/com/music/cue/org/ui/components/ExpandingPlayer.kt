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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.util.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.music.cue.org.data.Song
import kotlin.math.roundToInt
import kotlinx.coroutines.launch

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
    modifier: Modifier = Modifier,
) {
    if (song == null) return

    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
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
    var isOffsetInitialized by remember { mutableStateOf(value = false) }

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
    LaunchedEffect(key1 = pagerState.currentPage) {
        if (songs.isNotEmpty() && (songs[pagerState.currentPage].id != song.id)) {
            onSongSelected(songs[pagerState.currentPage])
        }
    }

    LaunchedEffect(swipeLimit) {
        if (swipeLimit > 0 && !isOffsetInitialized) {
            isOffsetInitialized = true
            offset.animateTo(swipeLimit, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessMediumLow))
        }
    }

    // Use derivedStateOf for values that change rapidly to avoid unnecessary recompositions
    val fractionState = remember(swipeLimit) {
        derivedStateOf {
            if (swipeLimit > 0) (1f - (offset.value / swipeLimit)).coerceIn(0f, 1f) else 1f
        }
    }

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
            val collapsedColor = MaterialTheme.colorScheme.primaryContainer
            val expandedColor = MaterialTheme.colorScheme.surface
            
            // Use Box instead of Surface to avoid recompositions when fraction changes
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .graphicsLayer { 
                        translationY = offset.value 
                        val f = fractionState.value
                        
                        // Apply shape and shadow via graphicsLayer to avoid recomposition
                        clip = true
                        shape = RoundedCornerShape(lerp(12f, 0f, f).dp)
                        shadowElevation = with(density) { 8.dp.toPx() }
                    }
                    // Animate background color smoothly in the draw phase
                    .drawBehind {
                        val f = fractionState.value
                        // Premium smooth color interpolation
                        val color = Color(
                            red = lerp(collapsedColor.red, expandedColor.red, f),
                            green = lerp(collapsedColor.green, expandedColor.green, f),
                            blue = lerp(collapsedColor.blue, expandedColor.blue, f),
                            alpha = lerp(collapsedColor.alpha, expandedColor.alpha, f)
                        )
                        drawRect(color)
                    }
                    // Optimize height and width using Modifier.layout
                    .layout { measurable, constraints ->
                        val f = fractionState.value
                        val sideMargin = lerp(16f, 0f, f).dp.roundToPx()
                        val w = (constraints.maxWidth - 2 * sideMargin).coerceAtLeast(0)
                        val h = lerp(collapsedHeightPx, containerHeightPx, f).roundToInt()
                        
                        val placeable = measurable.measure(
                            androidx.compose.ui.unit.Constraints.fixed(w, h)
                        )
                        
                        // Layout with the actual width to ensure side gaps
                        layout(w, h) {
                            placeable.place(0, 0)
                        }
                    }
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
                    }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = true,
                        beyondViewportPageCount = 1
                    ) { pageIndex ->
                        val currentSong = songs[pageIndex]
                        
                        // Optimize: pageOffset as a lambda to avoid recomposing all pages during swipe
                        val pageOffsetLambda = remember(pagerState, pageIndex) {
                            { (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction }
                        }

                        PlayerPageContent(
                            song = currentSong,
                            pageOffset = pageOffsetLambda,
                            fraction = { fractionState.value },
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
                    val topBarAlpha by remember {
                        derivedStateOf { ((fractionState.value - 0.5f) / 0.5f).coerceIn(0f, 1f) }
                    }
                    if (fractionState.value > 0.2f) {
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
                            modifier = Modifier.graphicsLayer { alpha = topBarAlpha }
                        )
                    }
                    
                    // Bottom progress (collapsed, static)
                    if (fractionState.value < 0.05f) {
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
