package com.music.cue.org.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.music.cue.org.data.Song
import com.music.cue.org.ui.theme.CueIcons
import dev.vivvvek.seeker.Seeker
import dev.vivvvek.seeker.SeekerDefaults
import kotlin.math.roundToInt

@Composable
fun PlayerPageContent(
    song: Song,
    pageOffset: () -> Float,
    fraction: () -> Float,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    configuration: android.content.res.Configuration,
    density: androidx.compose.ui.unit.Density,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Image Transition
        val baseImageSize = 300.dp
        val miniImageSize = 48.dp
        
        val miniX = with(density) { 8.dp.toPx() }
        val miniY = with(density) { 8.dp.toPx() }
        val fullX = with(density) { ((configuration.screenWidthDp.dp / 2) - (baseImageSize / 2)).toPx() }
        val fullY = with(density) { 100.dp.toPx() }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.albumArtUri)
                .crossfade(enable = true)
                .size(512, 512)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .offset { 
                    val f = fraction()
                    IntOffset(
                        lerp(miniX, fullX, f).roundToInt(),
                        lerp(miniY, fullY, f).roundToInt()
                    )
                }
                .graphicsLayer {
                    val f = fraction()
                    val targetScale = baseImageSize.value / miniImageSize.value
                    val scale = lerp(1f, targetScale, f)
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0f)
                    
                    // GPU-accelerated clip and shape
                    clip = true
                    val targetRadius = 16f / targetScale
                    shape = RoundedCornerShape(lerp(8f, targetRadius, f).dp)
                }
                .size(miniImageSize),
            contentScale = ContentScale.Crop
        )

        // Mini Controls (Slide with Pager)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .graphicsLayer { 
                    val f = fraction()
                    alpha = (1f - (f / 0.7f)).coerceIn(0f, 1f)
                    translationY = if (f > 0.8f) 10000f else 0f 
                }
                .padding(start = 64.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                MarqueeText(text = song.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = song.artist, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onPrevious) {
                Icon(CueIcons.SkipPrevious, null, tint = LocalContentColor.current)
            }
            IconButton(onClick = onTogglePlay) {
                Icon(
                    painter = if (isPlaying) CueIcons.PauseCircle else CueIcons.PlayCircle,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = LocalContentColor.current
                )
            }
            IconButton(onClick = onNext) {
                Icon(CueIcons.SkipNext, null, tint = LocalContentColor.current)
            }
        }

        // Full Controls (Pinned in Player Screen)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { 
                    val f = fraction()
                    val pOffset = pageOffset()
                    val offScreenFraction = if (pOffset < 0) -pOffset else pOffset
                    val controlsAlpha = (1f - offScreenFraction.coerceIn(0f, 1f))
                    
                    translationX = pOffset * size.width
                    alpha = ((f - 0.3f) / 0.7f).coerceIn(0f, 1f) * controlsAlpha
                    translationY = if (f < 0.2f) 10000f else 0f
                }
                .padding(top = 100.dp + baseImageSize + 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MarqueeText(text = song.title, style = MaterialTheme.typography.headlineMedium)
            Text(text = song.artist, style = MaterialTheme.typography.bodyLarge, color = LocalContentColor.current.copy(alpha = 0.7f))
            
            Spacer(modifier = Modifier.height(48.dp))

            Seeker(
                value = if (duration > 0) currentPosition.toFloat() else 0f,
                range = 0f..(duration.toFloat().coerceAtLeast(1f)),
                onValueChange = { onSeek(it.toLong()) },
                modifier = Modifier.fillMaxWidth(),
                colors = SeekerDefaults.seekerColors(
                    progressColor = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    thumbColor = MaterialTheme.colorScheme.primary
                )
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = formatTime(currentPosition), style = MaterialTheme.typography.labelMedium, color = LocalContentColor.current.copy(alpha = 0.7f))
                Text(text = formatTime(duration), style = MaterialTheme.typography.labelMedium, color = LocalContentColor.current.copy(alpha = 0.7f))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevious, modifier = Modifier.size(64.dp)) {
                    Icon(CueIcons.SkipPrevious, null, modifier = Modifier.size(40.dp), tint = LocalContentColor.current)
                }
                FilledIconButton(onClick = onTogglePlay, modifier = Modifier.size(80.dp)) {
                    Icon(
                        painter = if (isPlaying) CueIcons.PauseCircle else CueIcons.PlayCircle,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                IconButton(onClick = onNext, modifier = Modifier.size(64.dp)) {
                    Icon(CueIcons.SkipNext, null, modifier = Modifier.size(40.dp), tint = LocalContentColor.current)
                }
            }
        }
    }
}
