package com.music.cue.org.components


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.music.cue.org.data.bottomNavItems

@Composable
fun CueNavigation(
    modifier: Modifier = Modifier
) {

    // favorites, playlists, tracks, albums, artists, folders
    var selectItemIndex by remember { mutableIntStateOf(2) }
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier
    ) { contentPadding ->
        PrimaryScrollableTabRow(
            selectedTabIndex = selectItemIndex,
            modifier = Modifier.padding(contentPadding)
        ) {
            bottomNavItems.forEachIndexed { index, destination ->
                Tab(
                    selected = selectItemIndex == index,
                    onClick = {
                        navController.navigate(route = destination.route)
                        selectItemIndex = index
                    },
                    text = {
                        Text(
                            text = destination.title,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                )
            }
        }
    }
}


@Composable
@Preview
fun CueNavigationPreview() {
    CueNavigation()
}