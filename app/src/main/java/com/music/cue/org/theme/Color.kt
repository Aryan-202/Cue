package com.music.cue.org.theme

import androidx.compose.ui.graphics.Color

/**
 * Centralized color palette for the Cue music player
 * All colors are defined here for easy maintenance and theming
 */
object CueColors {
    // Primary Blue Palette
    val BluePrimary = Color(0xFF1976D2)      // Main blue
    val BlueLight = Color(0xFF63A4FF)         // Light blue
    val BlueDark = Color(0xFF004BA0)           // Dark blue
    val BlueAccent = Color(0xFF82B1FF)         // Accent blue
    val BlueSoft = Color(0xFFE3F2FD)           // Soft blue background
    val BlueTransparent = Color(0x801976D2)    // Transparent blue

    // Secondary Colors
    val CyanAccent = Color(0xFF00BCD4)          // Cyan for highlights
    val TealAccent = Color(0xFF1DE9B6)          // Teal for secondary actions

    // Neutral Colors
    val White = Color(0xFFFFFFFF)
    val OffWhite = Color(0xFFF5F5F5)
    val LightGray = Color(0xFFE0E0E0)
    val MediumGray = Color(0xFF9E9E9E)
    val DarkGray = Color(0xFF616161)
    val Black = Color(0xFF212121)

    // Semantic Colors
    val Success = Color(0xFF4CAF50)              // Green
    val Warning = Color(0xFFFFC107)               // Amber
    val Error = Color(0xFFD32F2F)                  // Red
    val Info = BlueLight                           // Info uses blue

    // Player Specific Colors
    val PlayerBackground = BlueDark
    val PlayerControls = White
    val PlayerProgress = BlueLight
    val PlayerSecondaryControls = LightGray

    // Favorite Colors
    val FavoriteActive = Color(0xFFFF4081)        // Pink for active favorite
    val FavoriteInactive = LightGray

    // Gradient Colors
    val GradientStart = BluePrimary
    val GradientEnd = BlueDark

    // Overlay Colors
    val ScrimBackground = Color(0x99000000)       // Semi-transparent black
    val OverlayLight = Color(0x33FFFFFF)           // Semi-transparent white
}

/**
 * Dark theme colors derived from blue palette
 */
object CueDarkColors {
    // Dark Theme Primary
    val BluePrimary = Color(0xFF42A5F5)           // Lighter blue for dark theme
    val BlueLight = Color(0xFF80D8FF)              // Very light blue
    val BlueDark = Color(0xFF1565C0)               // Dark blue base
    val BlueAccent = Color(0xFF90CAF9)              // Light accent

    // Dark Theme Backgrounds
    val Background = Color(0xFF121212)              // Material dark background
    val Surface = Color(0xFF1E1E1E)                  // Surface color
    val SurfaceVariant = Color(0xFF2D2D2D)           // Variant surface

    // Dark Theme Text
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xB3FFFFFF)            // 70% white
    val TextTertiary = Color(0x80FFFFFF)             // 50% white

    // Dark Theme Player
    val PlayerBackground = Color(0xFF0D0D0D)
    val PlayerControls = Color(0xDEFFFFFF)

    // Semantic Colors for Dark Theme
    val Success = Color(0xFF81C784)
    val Warning = Color(0xFFFFD54F)
    val Error = Color(0xFFE57373)
}