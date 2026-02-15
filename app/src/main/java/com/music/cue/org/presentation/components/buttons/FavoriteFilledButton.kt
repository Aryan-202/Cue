package com.music.cue.org.presentation.components.buttons

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
fun FavoriteFilledButton(
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
            painter = painterResource(id = R.drawable.favorite_filled_button),
            contentDescription = "Remove from favorites",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(name = "Favorite Filled Button - Default")
@Composable
fun FavoriteFilledButtonPreview() {
    FavoriteFilledButton(
        onClick = {}
    )
}

@Preview(name = "Favorite Filled Button - Disabled")
@Composable
fun FavoriteFilledButtonDisabledPreview() {
    FavoriteFilledButton(
        onClick = {},
        isEnabled = false
    )
}

@Preview(name = "Favorite Filled Button - Small")
@Composable
fun FavoriteFilledButtonSmallPreview() {
    FavoriteFilledButton(
        onClick = {},
        size = ButtonSize.SMALL
    )
}

@Preview(name = "Favorite Filled Button - Large")
@Composable
fun FavoriteFilledButtonLargePreview() {
    FavoriteFilledButton(
        onClick = {},
        size = ButtonSize.LARGE
    )
}