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
fun PlayButton(
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

    // Calculate icon size based on enum
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
            painter = painterResource(id = R.drawable.play_button),
            contentDescription = "Play",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(name = "Play Button - Default")
@Composable
fun PlayButtonPreview() {
    PlayButton(
        onClick = {}
    )
}

@Preview(name = "Play Button - Disabled")
@Composable
fun PlayButtonDisabledPreview() {
    PlayButton(
        onClick = {},
        isEnabled = false
    )
}

@Preview(name = "Play Button - Small")
@Composable
fun PlayButtonSmallPreview() {
    PlayButton(
        onClick = {},
        size = ButtonSize.SMALL
    )
}

@Preview(name = "Play Button - Large")
@Composable
fun PlayButtonLargePreview() {
    PlayButton(
        onClick = {},
        size = ButtonSize.LARGE
    )
}