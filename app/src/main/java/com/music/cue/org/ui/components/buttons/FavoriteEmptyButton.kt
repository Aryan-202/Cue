package com.music.cue.org.ui.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.music.cue.org.R
import com.music.cue.org.constants.ButtonSize

@Composable
fun FavoriteEmptyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    size: ButtonSize = ButtonSize.MEDIUM
) {
    val buttonSize = when (size) {
        ButtonSize.SMALL -> 32.dp
        ButtonSize.MEDIUM -> 48.dp
        ButtonSize.LARGE -> 64.dp
    }

    val iconSize = when (size) {
        ButtonSize.SMALL -> 16.dp
        ButtonSize.MEDIUM -> 24.dp
        ButtonSize.LARGE -> 32.dp
    }

    IconButton(
        onClick = onClick,
        modifier = modifier.size(buttonSize),
        enabled = isEnabled
    ) {
        Icon(
            painter = painterResource(id = R.drawable.favorite_empty_button),
            contentDescription = "Add to favorites",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(name = "Favorite Empty Button - Default")
@Composable
fun FavoriteEmptyButtonPreview() {
    FavoriteEmptyButton(
        onClick = {}
    )
}

@Preview(name = "Favorite Empty Button - Disabled")
@Composable
fun FavoriteEmptyButtonDisabledPreview() {
    FavoriteEmptyButton(
        onClick = {},
        isEnabled = false
    )
}

@Preview(name = "Favorite Empty Button - Small")
@Composable
fun FavoriteEmptyButtonSmallPreview() {
    FavoriteEmptyButton(
        onClick = {},
        size = ButtonSize.SMALL
    )
}

@Preview(name = "Favorite Empty Button - Large")
@Composable
fun FavoriteEmptyButtonLargePreview() {
    FavoriteEmptyButton(
        onClick = {},
        size = ButtonSize.LARGE
    )
}