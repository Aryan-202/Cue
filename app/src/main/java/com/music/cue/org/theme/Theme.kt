package com.music.cue.org.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Local composition for custom theme properties
 */
val LocalCueColors = staticCompositionLocalOf { CueColors }
val LocalCueDimens = staticCompositionLocalOf { CueDimens }
val LocalCueTypography = staticCompositionLocalOf { CueTypography }

/**
 * Main theme composable for Cue music player
 */
@Composable
fun CueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> createDarkColorScheme()
        else -> createLightColorScheme()
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        setupEdgeToEdge(view, darkTheme)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CueMaterialTypography,
        shapes = CueShapes,
        content = {
            CompositionLocalProvider(
                LocalCueColors provides if (darkTheme) createDarkCustomColors() else createLightCustomColors(),
                LocalCueDimens provides CueDimens,
                LocalCueTypography provides CueTypography,
                content = content
            )
        }
    )
}

/**
 * Light color scheme based on blue theme
 */
private fun createLightColorScheme(): ColorScheme {
    return lightColorScheme(
        primary = CueColors.BluePrimary,
        onPrimary = Color.White,
        primaryContainer = CueColors.BlueSoft,
        onPrimaryContainer = CueColors.BlueDark,

        secondary = CueColors.CyanAccent,
        onSecondary = Color.Black,
        secondaryContainer = CueColors.BlueAccent,
        onSecondaryContainer = CueColors.BlueDark,

        tertiary = CueColors.TealAccent,
        onTertiary = Color.Black,
        tertiaryContainer = CueColors.BlueLight,
        onTertiaryContainer = CueColors.BlueDark,

        background = Color.White,
        onBackground = Color.Black,

        surface = CueColors.OffWhite,
        onSurface = Color.Black,
        surfaceVariant = CueColors.LightGray,
        onSurfaceVariant = CueColors.DarkGray,

        error = CueColors.Error,
        onError = Color.White,
        errorContainer = CueColors.Error.copy(alpha = 0.12f),
        onErrorContainer = CueColors.Error,

        outline = CueColors.MediumGray,
        inverseSurface = CueColors.BlueDark,
        inverseOnSurface = Color.White,
        inversePrimary = CueColors.BlueLight,

        surfaceTint = CueColors.BluePrimary,
        outlineVariant = CueColors.LightGray,
        scrim = CueColors.ScrimBackground
    )
}

/**
 * Dark color scheme based on blue theme
 */
private fun createDarkColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = CueDarkColors.BluePrimary,
        onPrimary = Color.Black,
        primaryContainer = CueDarkColors.BlueDark,
        onPrimaryContainer = CueDarkColors.BlueLight,

        secondary = CueDarkColors.BlueAccent,
        onSecondary = Color.Black,
        secondaryContainer = CueDarkColors.BlueDark.copy(alpha = 0.7f),
        onSecondaryContainer = CueDarkColors.BlueLight,

        tertiary = CueDarkColors.BlueLight,
        onTertiary = Color.Black,
        tertiaryContainer = CueDarkColors.BlueDark,
        onTertiaryContainer = CueDarkColors.BlueLight,

        background = CueDarkColors.Background,
        onBackground = CueDarkColors.TextPrimary,

        surface = CueDarkColors.Surface,
        onSurface = CueDarkColors.TextPrimary,
        surfaceVariant = CueDarkColors.SurfaceVariant,
        onSurfaceVariant = CueDarkColors.TextSecondary,

        error = CueDarkColors.Error,
        onError = Color.Black,
        errorContainer = CueDarkColors.Error.copy(alpha = 0.12f),
        onErrorContainer = CueDarkColors.Error,

        outline = CueDarkColors.TextTertiary,
        inverseSurface = CueDarkColors.BlueLight,
        inverseOnSurface = Color.Black,
        inversePrimary = CueColors.BluePrimary,

        surfaceTint = CueDarkColors.BluePrimary,
        outlineVariant = CueDarkColors.SurfaceVariant,
        scrim = CueColors.ScrimBackground
    )
}

/**
 * Custom light colors for local composition
 */
private fun createLightCustomColors(): CueColors {
    return CueColors
}

/**
 * Custom dark colors for local composition
 */
private fun createDarkCustomColors(): CueColors {
    return CueColors // You can override specific colors for dark theme here if needed
}

/**
 * Material 3 shapes configuration
 */
val CueShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(CueDimens.radius_small),
    small = androidx.compose.foundation.shape.RoundedCornerShape(CueDimens.radius_medium),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(CueDimens.radius_large),
    large = androidx.compose.foundation.shape.RoundedCornerShape(CueDimens.radius_xlarge),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(CueDimens.radius_round)
)

/**
 * Setup edge-to-edge display
 */
private fun setupEdgeToEdge(view: android.view.View, darkTheme: Boolean) {
    val window = (view.context as? android.app.Activity)?.window ?: return
    WindowCompat.setDecorFitsSystemWindows(window, false)
    window.statusBarColor = android.graphics.Color.TRANSPARENT
    window.navigationBarColor = android.graphics.Color.TRANSPARENT

    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
    WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
}

/**
 * Helper objects to access theme properties
 */
object CueTheme {
    val colors: CueColors
        @Composable
        @ReadOnlyComposable
        get() = LocalCueColors.current

    val dimens: CueDimens
        @Composable
        @ReadOnlyComposable
        get() = LocalCueDimens.current

    val typography: CueTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalCueTypography.current


}