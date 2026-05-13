package com.music.cue.org.screens.home

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.music.cue.org.repos.SongRepos
import com.music.cue.org.screens.home.components.AlphabetStrip
import com.music.cue.org.screens.home.components.GroupListItem
import com.music.cue.org.screens.home.components.LetterOverlay
import com.music.cue.org.screens.home.components.SongListItem
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
    val songsTab by viewModel.songsTab.collectAsState()
    val artistsTab by viewModel.artistsTab.collectAsState()
    val albumsTab by viewModel.albumsTab.collectAsState()
    val foldersTab by viewModel.foldersTab.collectAsState()
    
    val selectedGroup by viewModel.selectedGroup.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val alphabetMap by viewModel.alphabetMap.collectAsState()
    
    val selectedSong by viewModel.selectedSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    
    val tabs = remember { HomeTab.entries }
    val pagerState = rememberPagerState(initialPage = tabs.indexOf(selectedTab)) { tabs.size }
    
    // Maintain separate scroll states for each tab
    val tabStates = remember { List(tabs.size) { androidx.compose.foundation.lazy.LazyListState() } }
    val coroutineScope = rememberCoroutineScope()

    // Sync Pager with selectedTab
    LaunchedEffect(selectedTab) {
        val index = tabs.indexOf(selectedTab)
        if (index >= 0 && index != pagerState.currentPage) {
            pagerState.animateScrollToPage(index)
        }
    }

    // Sync selectedTab with Pager
    LaunchedEffect(pagerState.currentPage) {
        val tab = tabs[pagerState.currentPage]
        if (tab != selectedTab) {
            viewModel.onTabSelected(tab)
        }
    }

    BackHandler(enabled = selectedGroup != null) {
        viewModel.navigateBack()
    }

    val selectedLetterState = remember { mutableStateOf<Char?>(null) }
    val alphabet = remember { ('A'..'Z').toList() + '#' }
    var columnHeight by remember { mutableIntStateOf(0) }

    // Optimization: Use current list state for scrolling
    val currentListState = tabStates[pagerState.currentPage]

    val updateScroll: (Float) -> Unit = remember(columnHeight, alphabet, alphabetMap, currentListState) {
        { y ->
            if (columnHeight > 0) {
                val index = (y / columnHeight * alphabet.size).toInt().coerceIn(0, alphabet.size - 1)
                val char = alphabet[index]
                if (selectedLetterState.value != char) {
                    selectedLetterState.value = char
                    alphabetMap[char]?.let { targetIndex ->
                        coroutineScope.launch {
                            currentListState.scrollToItem(targetIndex)
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
                    onTabSelected = { tab ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(tabs.indexOf(tab))
                        }
                    }
                )
            } else {
                Header(title = selectedGroup ?: "", onBack = { viewModel.navigateBack() })
            }

            if (selectedGroup == null) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    beyondViewportPageCount = 1
                ) { pageIndex ->
                    val currentTab = tabs[pageIndex]
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = tabStates[pageIndex]
                    ) {
                        when (currentTab) {
                            HomeTab.Songs -> {
                                itemsIndexed(
                                    items = songsTab,
                                    key = { _, song -> song.id },
                                    contentType = { _, _ -> "song_item" }
                                ) { index, song ->
                                    SongListItem(
                                        index = index,
                                        song = song,
                                        isPlaying = song.id == selectedSong?.id && isPlaying,
                                        onClick = {
                                            viewModel.onSongSelected(song)
                                            onSongClick()
                                        }
                                    )
                                }
                            }
                            HomeTab.Artists -> {
                                itemsIndexed(
                                    items = artistsTab,
                                    key = { _, item -> item.name },
                                    contentType = { _, _ -> "group_item" }
                                ) { index, item ->
                                    GroupListItem(index, item, currentTab) { viewModel.onGroupSelected(item.name) }
                                }
                            }
                            HomeTab.Albums -> {
                                itemsIndexed(
                                    items = albumsTab,
                                    key = { _, item -> item.name },
                                    contentType = { _, _ -> "group_item" }
                                ) { index, item ->
                                    GroupListItem(index, item, currentTab) { viewModel.onGroupSelected(item.name) }
                                }
                            }
                            HomeTab.Folders -> {
                                itemsIndexed(
                                    items = foldersTab,
                                    key = { _, item -> item.name },
                                    contentType = { _, _ -> "group_item" }
                                ) { index, item ->
                                    GroupListItem(index, item, currentTab) { viewModel.onGroupSelected(item.name) }
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = currentListState
                ) {
                    itemsIndexed(
                        items = songs,
                        key = { _, song -> song.id },
                        contentType = { _, _ -> "song_item" }
                    ) { index, song ->
                        SongListItem(
                            index = index,
                            song = song,
                            isPlaying = song.id == selectedSong?.id && isPlaying,
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
private fun Header(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 8.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
