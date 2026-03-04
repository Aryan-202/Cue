package com.music.cue.org.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueFonts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CueTopBar(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding( vertical = 16.dp ),
    ) {
        // App Title
        Text(
            text = "Cue Music",
            modifier = Modifier.padding(start = 16.dp),
            fontFamily = CueFonts.RobotoCondensed,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { newText -> searchQuery = newText },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = CueColors.BlueAccent
                )
            },
            placeholder = {
                Text(
                    text = "Search...",
                    fontFamily = CueFonts.Roboto,
                    fontSize = 14.sp
                )
            },
            shape = RoundedCornerShape(25.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = CueColors.BlueAccent.copy(alpha = 0.1f),
                unfocusedContainerColor = CueColors.BlueAccent.copy(alpha = 0.1f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = CueColors.BlueAccent
            ),
            singleLine = true,
            textStyle = TextStyle(
                fontFamily = CueFonts.Roboto,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
@Preview(showBackground = true)
fun CueTopBarPreview() {
    CueTopBar()
}