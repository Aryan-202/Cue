package com.music.cue.org.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.music.cue.org.components.ArtistsGrid
import com.music.cue.org.components.CueNavigation
import com.music.cue.org.components.CueTopBar

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
            .padding(3.dp)
    ) {
        CueTopBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        )
        ArtistsGrid(searchQuery = searchQuery)
        CueNavigation()
    }
}
