package com.music.cue.org.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


/**
 * Typography system for Cue music player
 * Scalable text styles following Material Design 3 guidelines
 */
object CueTypography {

    // Display Styles - For large headers
    val displayLarge = TextStyle(
        fontFamily = CueFonts.RobotoCondensed,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    )

    val displayMedium = TextStyle(
        fontFamily = CueFonts.RobotoCondensed,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    )

    val displaySmall = TextStyle(
        fontFamily = CueFonts.RobotoCondensed,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    )

    // Headline Styles - For screen titles
    val headlineLarge = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    )

    val headlineMedium = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    )

    val headlineSmall = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    )

    // Title Styles - For section headers
    val titleLarge = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

    val titleMedium = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

    val titleSmall = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

    // Label Styles - For buttons and tags
    val labelLarge = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )

    val labelMedium = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

    val labelSmall = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

    // Body Styles - For main content
    val bodyLarge = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )

    val bodyMedium = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )

    val bodySmall = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    )

    // Player Specific Text Styles
    val songTitle = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

    val artistName = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = CueColors.MediumGray
    )

    val durationText = TextStyle(
        fontFamily = CueFonts.RobotoMono,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        color = CueColors.MediumGray
    )

    val currentTimeText = TextStyle(
        fontFamily = CueFonts.RobotoMono,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    )

    val totalTimeText = TextStyle(
        fontFamily = CueFonts.RobotoMono,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = CueColors.MediumGray
    )

    val playlistName = TextStyle(
        fontFamily = CueFonts.RobotoCondensed,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    )

    val albumName = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    )

    val songCount = TextStyle(
        fontFamily = CueFonts.Roboto,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        color = CueColors.MediumGray
    )
}

/**
 * Material 3 Typography implementation
 **/
val CueMaterialTypography = Typography(
    displayLarge = CueTypography.displayLarge,
    displayMedium = CueTypography.displayMedium,
    displaySmall = CueTypography.displaySmall,

    headlineLarge = CueTypography.headlineLarge,
    headlineMedium = CueTypography.headlineMedium,
    headlineSmall = CueTypography.headlineSmall,

    titleLarge = CueTypography.titleLarge,
    titleMedium = CueTypography.titleMedium,
    titleSmall = CueTypography.titleSmall,

    labelLarge = CueTypography.labelLarge,
    labelMedium = CueTypography.labelMedium,
    labelSmall = CueTypography.labelSmall,

    bodyLarge = CueTypography.bodyLarge,
    bodyMedium = CueTypography.bodyMedium,
    bodySmall = CueTypography.bodySmall
)