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
import com.music.cue.org.domain.enums.RepeatMode

@Composable
fun RepeatButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: ButtonSize = ButtonSize.MEDIUM,
    buttonMode: RepeatMode = RepeatMode.OFF,
) {

    val iconSize = when (size) {
        ButtonSize.SMALL -> 16.dp
        ButtonSize.MEDIUM -> 24.dp
        ButtonSize.LARGE -> 32.dp
    }

    val iconState = when (buttonMode) {
        RepeatMode.OFF -> painterResource(id = R.drawable.repeat_off_button)
        RepeatMode.REPEAT_ALL -> painterResource(id = R.drawable.repeat_on_button)
        RepeatMode.REPEAT_ONE -> painterResource(id = R.drawable.repeat_one_button)
    }

    val buttonSize = when (size) {
        ButtonSize.SMALL -> 32.dp
        ButtonSize.MEDIUM -> 48.dp
        ButtonSize.LARGE -> 64.dp
    }

    IconButton(
        onClick = onClick,
        modifier = modifier.size(buttonSize),
    ) {
        Icon(
            painter = iconState,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(iconSize)
        )
    }

}

@Preview
@Composable
fun RepeatButtonOffPreview() {
    RepeatButton(
        onClick = {},
        buttonMode = RepeatMode.OFF
    )
}

@Preview
@Composable
fun RepeatButtonAllPreview() {
    RepeatButton(
        onClick = {},
        buttonMode = RepeatMode.REPEAT_ALL
    )
}

@Preview
@Composable
fun RepeatButtonOnePreview() {
    RepeatButton(
        onClick = {},
        buttonMode = RepeatMode.REPEAT_ONE
    )
}