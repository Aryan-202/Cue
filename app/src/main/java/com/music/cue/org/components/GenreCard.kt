package com.music.cue.org.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.music.cue.org.R

@Composable
fun GenreCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = modifier
                    .clip(RectangleShape)
            )
            Text(
                text = "Genre Name"
            )
        }
    }
}

@Composable
@Preview
fun GenreCardPreview() {
    GenreCard()
}
