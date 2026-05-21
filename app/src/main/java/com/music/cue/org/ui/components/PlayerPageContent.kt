package com.music.cue.org.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.music.cue.org.data.Song
import com.music.cue.org.ui.theme.CueIcons
import kotlin.math.roundToInt

@Composable
fun PlayerPageContent(
    song: Song,
    pageOffset: Float,
    fraction: Float,
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

        val currentImageX = lerp(miniX, fullX, fraction)
        val currentImageY = lerp(miniY, fullY, fraction)

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.albumArtUri)
                .crossfade(enable = true)
                .size(Size.ORIGINAL)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .offset { IntOffset(currentImageX.roundToInt(), currentImageY.roundToInt()) }
                .size(lerp(miniImageSize.value, baseImageSize.value, fraction).dp)
                .clip(RoundedCornerShape(lerp(8f, 16f, fraction).dp)),
            contentScale = ContentScale.Crop
        )

        // Mini Controls (Slide with Pager)
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
                    Icon(CueIcons.SkipPrevious, null)
                }
                IconButton(onClick = onTogglePlay) {
                    Icon(painter = if (isPlaying) CueIcons.PauseCircle else CueIcons.PlayCircle, contentDescription = null, modifier = Modifier.size(32.dp))
                }
                IconButton(onClick = onNext) {
                    Icon(CueIcons.SkipNext, null)
                }
            }
        }

        // Full Controls (Pinned in Player Screen)
        if (fraction > 0.2f) {
            val offScreenFraction = if (pageOffset < 0) -pageOffset else pageOffset
            val controlsAlpha = (1f - offScreenFraction.coerceIn(0f, 1f))
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { 
                        // Counteract the slide ONLY for full controls in player screen
                        translationX = pageOffset * size.width
                        alpha = ((fraction - 0.2f) / 0.8f).coerceIn(0f, 1f) * controlsAlpha
                    }
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
                        Icon(CueIcons.SkipPrevious, null, modifier = Modifier.size(40.dp))
                    }
                    FilledIconButton(onClick = onTogglePlay, modifier = Modifier.size(80.dp)) {
                        Icon(painter = if (isPlaying) CueIcons.PauseCircle else CueIcons.PlayCircle, contentDescription = null, modifier = Modifier.size(48.dp))
                    }
                    IconButton(onClick = onNext, modifier = Modifier.size(64.dp)) {
                        Icon(CueIcons.SkipNext, null, modifier = Modifier.size(40.dp))
                    }
                }
            }
        }
    }
}
