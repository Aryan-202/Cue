package com.music.cue.org.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.music.cue.org.screens.home.HomeTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationBar(
    selectedTab: HomeTab,
    onTabSelected: (HomeTab) -> Unit
) {
    val tabs = HomeTab.entries
    SecondaryScrollableTabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        edgePadding = 16.dp,
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(selectedTab.ordinal, matchContentSize = true)
            )
        },
        tabs = {
            tabs.forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    text = { Text(text = tab.name) }
                )
            }
        }
    )
}
