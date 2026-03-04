package com.music.cue.org.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.music.cue.org.data.fetchLocalArtists

@Composable
fun ArtistsGrid (
    searchQuery: String,
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current
    val allArtists by remember {
        mutableStateOf(fetchLocalArtists(context))
    }

    val filteredArtists = if (searchQuery.isEmpty()) {
        allArtists
    } else {
        allArtists.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(filteredArtists) { artist ->
            ArtistGridCard(artist = artist)
        }

    }
}


@Preview
@Composable
fun ArtistsGridPreview () {
    ArtistsGrid(searchQuery = "")
}