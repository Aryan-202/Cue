package com.music.cue.org.screens.home.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlphabetStrip(
    alphabet: List<Char>,
    alphabetMap: Map<Char, Int>,
    onPositionChanged: (Int) -> Unit,
    onUpdateScroll: (Float) -> Unit,
    onDragFinished: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(end = 4.dp, top = 64.dp, bottom = 16.dp)
            .width(24.dp)
            .onGloballyPositioned { onPositionChanged(it.size.height) }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        onUpdateScroll(offset.y)
                        tryAwaitRelease()
                        onDragFinished()
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> onUpdateScroll(offset.y) },
                    onDrag = { change, _ -> 
                        change.consume()
                        onUpdateScroll(change.position.y) 
                    },
                    onDragEnd = onDragFinished,
                    onDragCancel = onDragFinished
                )
            },
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        alphabet.forEach { char ->
            val hasSongs = alphabetMap.containsKey(char)
            Text(
                text = char.toString(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontWeight = if (hasSongs) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (hasSongs) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                modifier = Modifier.padding(vertical = 1.dp)
            )
        }
    }
}
