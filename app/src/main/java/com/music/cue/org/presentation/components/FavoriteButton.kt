package com.music.cue.org.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueIcons

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(
                if (isFavorite) CueIcons.FavoriteFilled
                else CueIcons.FavoriteEmpty
            ),
            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
            tint = if (isFavorite) CueColors.FavoriteActive else CueColors.MediumGray,
            modifier = Modifier.size(20.dp)
        )
    }
}