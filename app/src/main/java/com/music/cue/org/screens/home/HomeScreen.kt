package com.music.cue.org.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.music.cue.org.repos.SongRepos
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel(
        factory = HomeScreenViewModelFactory(SongRepos(LocalContext.current))
    ),
    onSongClick: () -> Unit = {}
) {
    val songs by viewModel.songs.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selectedLetter by remember { mutableStateOf<Char?>(null) }

    val alphabetMap = remember(songs) {
        songs.mapIndexed { index, song ->
            val char = song.title.firstOrNull()?.uppercaseChar() ?: '#'
            char to index
        }.distinctBy { it.first }.toMap()
    }

    val alphabet = remember { ('A'..'Z').toList() + '#' }
    var columnHeight by remember { mutableIntStateOf(0) }

    fun updateScroll(y: Float) {
        if (columnHeight > 0) {
            val index = (y / columnHeight * alphabet.size).toInt().coerceIn(0, alphabet.size - 1)
            val char = alphabet[index]
            if (selectedLetter != char) {
                alphabetMap[char]?.let { songIndex ->
                    coroutineScope.launch {
                        selectedLetter = char
                        listState.scrollToItem(songIndex)
                    }
                } ?: run {
                    selectedLetter = char
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            itemsIndexed(songs) { index, song ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = song.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    supportingContent = {
                        Text(
                            text = song.artist,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = (index + 1).toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(24.dp),
                                textAlign = TextAlign.End
                            )
                            AsyncImage(
                                model = song.albumArtUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop,
                                placeholder = rememberVectorPainter(Icons.Default.MusicNote),
                                error = rememberVectorPainter(Icons.Default.MusicNote)
                            )
                        }
                    },
                    modifier = Modifier.clickable {
                        viewModel.onSongSelected(song)
                        onSongClick()
                    }
                )
            }
        }

        // Alphabet Strip
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .padding(end = 4.dp, top = 16.dp, bottom = 16.dp)
                .width(24.dp)
                .onGloballyPositioned { columnHeight = it.size.height }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            updateScroll(offset.y)
                            tryAwaitRelease()
                            selectedLetter = null
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> updateScroll(offset.y) },
                        onDrag = { change, _ -> updateScroll(change.position.y) },
                        onDragEnd = { selectedLetter = null },
                        onDragCancel = { selectedLetter = null }
                    )
                },
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            alphabet.forEach { char ->
                val hasSongs = alphabetMap.containsKey(char)
                Text(
                    text = char.toString(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = if (hasSongs) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (hasSongs) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }

        // Center Overlay for selected letter
        selectedLetter?.let { letter ->
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = letter.toString(),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
