package com.music.cue.org.presentation.components.buttons

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.music.cue.org.R
import com.music.cue.org.core.constants.ButtonSize


@Composable
fun SkipNextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
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
        modifier = modifier.size(buttonSize)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.skip_next_circle_button),
            contentDescription = "Skip Next",
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview
@Composable
fun SkipNextButtonPreview() {
    SkipNextButton(onClick = {})
}

