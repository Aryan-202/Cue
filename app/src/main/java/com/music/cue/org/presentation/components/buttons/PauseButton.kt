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
import com.music.cue.org.core.constants.ButtonSize

@Composable
fun PauseButton(
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
            painter = painterResource(id = R.drawable.pause_circle),
            contentDescription = "Pause",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(name = "Pause Button - Default")
@Composable
fun PauseButtonPreview() {
    PauseButton(
        onClick = {}
    )
}

@Preview(name = "Pause Button - Disabled")
@Composable
fun PauseButtonDisabledPreview() {
    PauseButton(
        onClick = {},
        isEnabled = false
    )
}

@Preview(name = "Pause Button - Small")
@Composable
fun PauseButtonSmallPreview() {
    PauseButton(
        onClick = {},
        size = ButtonSize.SMALL
    )
}

@Preview(name = "Pause Button - Large")
@Composable
fun PauseButtonLargePreview() {
    PauseButton(
        onClick = {},
        size = ButtonSize.LARGE
    )
}