package com.music.cue.org.screens.home

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil.request.ImageRequest
import com.music.cue.org.data.Song
import com.music.cue.org.repos.SongRepos
import com.music.cue.org.ui.components.NavigationBar
import com.music.cue.org.utils.UserPrefs
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: android.content.Context = LocalContext.current,
    viewModel: HomeScreenViewModel = viewModel(
        factory = HomeScreenViewModelFactory(SongRepos(context), UserPrefs(context))
    ),
    onSongClick: () -> Unit = {}
) {
    val songs by viewModel.songs.collectAsState()
    val groupedItems by viewModel.groupedItems.collectAsState()
    val selectedGroup by viewModel.selectedGroup.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val alphabetMap by viewModel.alphabetMap.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    val selectedLetterState = remember { mutableStateOf<Char?>(null) }
    val alphabet = remember { ('A'..'Z').toList() + '#' }
    var columnHeight by remember { mutableIntStateOf(0) }

    BackHandler(enabled = selectedGroup != null) {
        viewModel.navigateBack()
    }

    val updateScroll: (Float) -> Unit = remember(columnHeight, alphabet, alphabetMap) {
        { y ->
            if (columnHeight > 0) {
                val index = (y / columnHeight * alphabet.size).toInt().coerceIn(0, alphabet.size - 1)
                val char = alphabet[index]
                if (selectedLetterState.value != char) {
                    selectedLetterState.value = char
                    alphabetMap[char]?.let { targetIndex ->
                        coroutineScope.launch {
                            listState.scrollToItem(targetIndex)
                        }
                    }
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (selectedGroup == null) {
                NavigationBar(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.onTabSelected(it) }
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.navigateBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = selectedGroup ?: "",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(start = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                if (selectedGroup == null && selectedTab != HomeTab.Songs) {
                    itemsIndexed(
                        items = groupedItems,
                        key = { _, item -> item.name },
                        contentType = { _, _ -> "group_item" }
                    ) { index, item ->
                        GroupListItem(
                            index = index,
                            item = item,
                            tab = selectedTab,
                            onClick = { viewModel.onGroupSelected(item.name) }
                        )
                    }
                } else {
                    itemsIndexed(
                        items = songs,
                        key = { _, song -> song.id },
                        contentType = { _, _ -> "song_item" } 
                    ) { index, song ->
                        SongListItem(
                            index = index,
                            song = song,
                            onClick = {
                                viewModel.onSongSelected(song)
                                onSongClick()
                            }
                        )
                    }
                }
            }
        }

        AlphabetStrip(
            alphabet = alphabet,
            alphabetMap = alphabetMap,
            onPositionChanged = { columnHeight = it },
            onUpdateScroll = updateScroll,
            onDragFinished = { selectedLetterState.value = null }
        )

        LetterOverlay(selectedLetterState)
    }
}

@Composable
fun GroupListItem(index: Int, item: GroupedItem, tab: HomeTab, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = (index + 1).toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(28.dp),
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.width(12.dp))

        val icon = when (tab) {
            HomeTab.Artists -> Icons.Default.Person
            HomeTab.Folders -> Icons.Default.Folder
            else -> Icons.Default.MusicNote
        }
        
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (item.artworkUri != null && tab == HomeTab.Albums) {
                AsyncImage(
                    model = item.artworkUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${item.songCount} songs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SongListItem(index: Int, song: Song, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = (index + 1).toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(28.dp),
            textAlign = TextAlign.End
        )
        
        Spacer(modifier = Modifier.width(12.dp))

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song.albumArtUri)
                .crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = rememberVectorPainter(Icons.Default.MusicNote),
            error = rememberVectorPainter(Icons.Default.MusicNote)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AlphabetStrip(
    alphabet: List<Char>,
    alphabetMap: Map<Char, Int>,
    onPositionChanged: (Int) -> Unit,
    onUpdateScroll: (Float) -> Unit,
    onDragFinished: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(end = 4.dp, top = 64.dp, bottom = 16.dp)
            .width(24.dp)
            .onGloballyPositioned { onPositionChanged(it.size.height) }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        onUpdateScroll(offset.y)
                        tryAwaitRelease()
                        onDragFinished()
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> onUpdateScroll(offset.y) },
                    onDrag = { change, _ -> 
                        change.consume()
                        onUpdateScroll(change.position.y) 
                    },
                    onDragEnd = onDragFinished,
                    onDragCancel = onDragFinished
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
                color = if (hasSongs) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.padding(vertical = 1.dp)
            )
        }
    }
}

@Composable
fun LetterOverlay(selectedLetterState: State<Char?>) {
    val letter = selectedLetterState.value
    if (letter != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
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
