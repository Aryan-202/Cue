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
import com.music.cue.org.domain.enums.ButtonSize


@Composable
fun ShuffleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = false,
    size: ButtonSize = ButtonSize.MEDIUM,
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
        if (isEnabled) {
            Icon(
                painter = painterResource(id = R.drawable.shuffle_button),
                contentDescription = "Shuffle",
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.primary,
            )
        } else {
            Icon (
                painter = painterResource(id = R.drawable.shuffle_button),
                contentDescription = "Shuffle",
                modifier = Modifier.size(iconSize),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }

}

@Composable
@Preview
fun ShuffleButtonEnabledPreview() {
    ShuffleButton(
        onClick = {},
        isEnabled = true
    )
}

@Composable
@Preview
fun ShuffleButtonDisabledPreview() {
    ShuffleButton(
        onClick = {},
        isEnabled = false
    )
}