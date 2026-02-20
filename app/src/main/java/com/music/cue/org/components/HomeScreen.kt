package com.music.cue.org.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueShapes

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 6.dp)
    ) {
        SearchBar(
            modifier = modifier
                .fillMaxWidth()
                .border(1.dp, CueColors.MediumGray, shape = CueShapes.medium)
                .clip(CueShapes.medium)
        )
        FavoriteSongs(
            modifier = modifier
                .padding(top = 5.dp)
        )
        LazyHorizontalGrid(
            rows = GridCells.Fixed(2),
            modifier = modifier
                .padding(top = 5.dp)
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}