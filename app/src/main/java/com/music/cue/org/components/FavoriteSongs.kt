package com.music.cue.org.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.music.cue.org.R
import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueFonts

@Composable
fun FavoriteSongs(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier

    ) {
        Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = null,
            Modifier
                .clip(CircleShape)
                .size(56.dp),
            contentScale = ContentScale.Crop
        )
        Text(
            text = "fav song temp",
            color = CueColors.FavoriteActive,
            fontStyle = FontStyle.Italic,
            fontFamily = CueFonts.Roboto
        )
    } }
}

@Composable
@Preview
fun FavoriteSongsPreview() {
    FavoriteSongs()
}


