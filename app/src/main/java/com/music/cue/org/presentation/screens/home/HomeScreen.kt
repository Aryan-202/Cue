package com.music.cue.org.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.music.cue.org.presentation.components.EmptyState
import com.music.cue.org.presentation.components.ErrorState
import com.music.cue.org.presentation.components.GenreCard
import com.music.cue.org.presentation.components.HorizontalSongCard
import com.music.cue.org.presentation.components.SearchBar
import com.music.cue.org.presentation.components.SectionHeader
import com.music.cue.org.presentation.components.VerticalSongCard
import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueShapes

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    ),
    onSongClick: (Long) -> Unit = {},
    onGenreClick: (Long) -> Unit = {},
    onSearchClick: () -> Unit = {}
) {
    val mostPlayedState by viewModel.mostPlayedSongs.collectAsStateWithLifecycle()
    val genresState by viewModel.genres.collectAsStateWithLifecycle()
    val recentlyPlayedState by viewModel.recentlyPlayed.collectAsStateWithLifecycle()
    val recommendedState by viewModel.recommendedForYou.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with greeting
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            CueColors.BluePrimary.copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Good evening,",
                    color = CueColors.MediumGray,
                    fontSize = 14.sp
                )
                Text(
                    text = "Music Lover",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            }
        }

        // Main Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Most Played Section
            item {
                SectionHeader(
                    title = "Most Played",
                    onSeeAllClick = { viewModel.onSeeAllClick("most_played") }
                )
            }

            item {
                when (val state = mostPlayedState) {
                    is HomeUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = CueColors.BluePrimary)
                        }
                    }
                    is HomeUiState.Success -> {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.data) { song ->
                                HorizontalSongCard(
                                    song = song,
                                    onClick = { viewModel.onSongClick(song) }
                                )
                            }
                        }
                    }
                    is HomeUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error loading songs",
                                color = CueColors.Error
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Genres Section
            item {
                SectionHeader(
                    title = "Browse Genres",
                    onSeeAllClick = { viewModel.onSeeAllClick("genres") }
                )
            }

            item {
                when (val state = genresState) {
                    is HomeUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = CueColors.BluePrimary)
                        }
                    }
                    is HomeUiState.Success -> {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.data) { genre ->
                                GenreCard(
                                    genre = genre,
                                    onClick = { viewModel.onGenreClick(genre) },
                                    modifier = Modifier.width(120.dp)
                                )
                            }
                        }
                    }
                    is HomeUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error loading genres",
                                color = CueColors.Error
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Recently Played Section
            item {
                SectionHeader(
                    title = "Recently Played",
                    onSeeAllClick = { viewModel.onSeeAllClick("recently_played") }
                )
            }

            item {
                when (val state = recentlyPlayedState) {
                    is HomeUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = CueColors.BluePrimary)
                        }
                    }
                    is HomeUiState.Success -> {
                        if (state.data.isEmpty()) {
                            EmptyState(
                                message = "No recently played songs",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .padding(16.dp)
                            )
                        } else {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.data) { song ->
                                    HorizontalSongCard(
                                        song = song,
                                        onClick = { viewModel.onSongClick(song) }
                                    )
                                }
                            }
                        }
                    }
                    is HomeUiState.Error -> {
                        ErrorState(
                            message = state.message,
                            onRetry = { /* Retry loading */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(16.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Recommended For You Section
            item {
                SectionHeader(
                    title = "Recommended For You",
                    onSeeAllClick = { viewModel.onSeeAllClick("recommended") }
                )
            }

            // Recommended songs as vertical list for variety
            when (val state = recommendedState) {
                is HomeUiState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = CueColors.BluePrimary)
                        }
                    }
                }
                is HomeUiState.Success -> {
                    items(state.data.size) { index ->
                        val song = state.data[index]
                        VerticalSongCard(
                            song = song,
                            onClick = { viewModel.onSongClick(song) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
                is HomeUiState.Error -> {
                    item {
                        ErrorState(
                            message = state.message,
                            onRetry = { /* Retry loading */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(16.dp)
                        )
                    }
                }
            }
        }

        // Search Bar at the bottom
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.onSearchQueryChanged(it) },
            onSearch = { onSearchClick() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(CueShapes.medium)
        )
    }
}

