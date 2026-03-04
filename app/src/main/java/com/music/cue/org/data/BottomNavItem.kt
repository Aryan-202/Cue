package com.music.cue.org.data

import androidx.annotation.DrawableRes
import com.music.cue.org.theme.CueIcons

data class BottomNavItem(
    val title: String,
    val route: String,
    @param:DrawableRes val icon: Int,
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Favorites",
        route = "favorites",
        icon = CueIcons.AppIcon,
    ),
    BottomNavItem(
        title = "Playlists",
        route = "playlists",
        icon = CueIcons.AppIcon,
    ),
    BottomNavItem(
        title = "Tracks",
        route = "tracks",
        icon = CueIcons.AppIcon,
    ),
    BottomNavItem(
        title = "Albums",
        route = "albums",
        icon = CueIcons.AppIcon,
    ),
    BottomNavItem(
        title = "Artists",
        route = "artists",
        icon = CueIcons.AppIcon,
    ),
    BottomNavItem(
        title = "Folders",
        route = "folders",
        icon = CueIcons.AppIcon,
    ),
)