package com.music.cue.org.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.music.cue.org.R
import com.music.cue.org.theme.CueColors
import com.music.cue.org.theme.CueTheme

@Composable
fun SearchBar(
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.search_button),
                contentDescription = "Search",
                tint = CueColors.MediumGray
            )
        },
        placeholder = {
            Text(
                text = "Search songs, albums, artists...",
                color = CueColors.MediumGray
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                if (searchQuery.isNotBlank()) {
                    onSearch(searchQuery)
                    focusManager.clearFocus()
                }
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = CueColors.BluePrimary,
            unfocusedIndicatorColor = CueColors.MediumGray,
            cursorColor = CueColors.BluePrimary
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    )
}

@Composable
@Preview
fun SearchBarPreview() {
    CueTheme {
        SearchBar(onSearch = {})
    }
}