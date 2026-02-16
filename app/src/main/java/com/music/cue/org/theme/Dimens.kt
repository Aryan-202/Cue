package com.music.cue.org.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centralized dimensions for consistent spacing and sizing
 * Follows 8dp grid system for scalability
 */
object CueDimens {

    // Base Grid Unit (8dp)
    private const val GRID_UNIT = 8

    // Spacing - Based on 8dp grid
    val spacing_0 = 0.dp
    val spacing_2 = 2.dp
    val spacing_4 = 4.dp
    val spacing_8 = 8.dp
    val spacing_12 = 12.dp
    val spacing_16 = 16.dp
    val spacing_24 = 24.dp
    val spacing_32 = 32.dp
    val spacing_40 = 40.dp
    val spacing_48 = 48.dp
    val spacing_56 = 56.dp
    val spacing_64 = 64.dp
    val spacing_72 = 72.dp
    val spacing_80 = 80.dp

    // Corner Radius
    val radius_none = 0.dp
    val radius_small = 4.dp
    val radius_medium = 8.dp
    val radius_large = 12.dp
    val radius_xlarge = 16.dp
    val radius_round = 100.dp

    // Icon Sizes
    val icon_extra_small = 16.dp
    val icon_small = 20.dp
    val icon_medium = 24.dp
    val icon_large = 32.dp
    val icon_extra_large = 48.dp
    val icon_xxl = 64.dp
    val icon_player_control = 48.dp
    val icon_player_large = 72.dp

    // Image Sizes
    val album_art_tiny = 40.dp
    val album_art_small = 48.dp
    val album_art_medium = 56.dp
    val album_art_large = 64.dp
    val album_art_xlarge = 128.dp
    val album_art_player = 280.dp
    val album_art_now_playing = 300.dp

    // Heights
    val height_button = 48.dp
    val height_mini_player = 72.dp
    val height_bottom_nav = 56.dp
    val height_top_bar = 56.dp
    val height_slider = 48.dp
    val height_divider = 1.dp

    // Widths
    val width_drawer = 280.dp
    val width_modal = 320.dp

    // Elevation
    val elevation_none = 0.dp
    val elevation_tiny = 1.dp
    val elevation_small = 2.dp
    val elevation_medium = 4.dp
    val elevation_large = 8.dp
    val elevation_xlarge = 16.dp

    // Player Specific
    val player_progress_height = 4.dp
    val player_control_spacing = 24.dp
    val player_play_button_size = 64.dp
    val player_mini_height = 64.dp

    // Font Sizes
    val font_caption = 12.sp
    val font_body_small = 14.sp
    val font_body = 16.sp
    val font_subtitle = 18.sp
    val font_title = 20.sp
    val font_headline = 24.sp
    val font_display = 32.sp

    // Padding
    val padding_screen_horizontal = 16.dp
    val padding_screen_top = 16.dp
    val padding_screen_bottom = 16.dp
    val padding_card = 12.dp
    val padding_item = 8.dp

    // List Specific
    val list_item_height = 56.dp
    val list_item_icon_size = 24.dp
    val list_divider_thickness = 1.dp

    // Grid Layouts
    val grid_columns_small = 2
    val grid_columns_medium = 3
    val grid_columns_large = 4
    val grid_item_spacing = 8.dp
}

/**
 * Responsive dimensions based on screen size
 */
object ResponsiveDimens {

    // Screen breakpoints
    val mobile_breakpoint = 600.dp
    val tablet_breakpoint = 840.dp

    // Dynamic spacing
    fun getScreenPadding(width: Int): Int {
        return when {
            width < 600 -> 16  // Mobile
            width < 840 -> 24  // Tablet
            else -> 32         // Desktop
        }
    }

    // Dynamic grid columns
    fun getGridColumns(width: Int): Int {
        return when {
            width < 600 -> 2    // Mobile
            width < 840 -> 3    // Tablet
            else -> 4           // Desktop/Large Tablet
        }
    }

    // Dynamic album art size
    fun getAlbumArtSize(width: Int): Int {
        return when {
            width < 600 -> 280  // Mobile
            width < 840 -> 360  // Tablet
            else -> 480         // Desktop
        }
    }
}