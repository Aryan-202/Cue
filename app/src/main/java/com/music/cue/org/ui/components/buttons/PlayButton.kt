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
    val iconSize = when (size) {
        ButtonSize.SMALL -> 24.dp
        ButtonSize.MEDIUM -> 32.dp
        ButtonSize.LARGE -> 48.dp
    }

    IconButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.size(iconSize + 16.dp) // Add padding
    ) {
        Icon(
            painter = painterResource(id = R.drawable.play_button),
            contentDescription = "Play",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(iconSize)
        )
    }
}



@Preview
@Composable
fun PlayButtonPreview() {
    PlayButton(
        onClick = {}
    )
}

