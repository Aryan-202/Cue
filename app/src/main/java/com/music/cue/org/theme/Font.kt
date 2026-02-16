package com.music.cue.org.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.music.cue.org.R

/**
 * Centralized font configuration for Cue music player
 * Uses Google Font Provider for Roboto family fonts
 */
object CueFonts {

    // Google Font Provider configuration
    private val provider = GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )

    // Font definitions
    private val roboto = GoogleFont("Roboto")
    private val robotoCondensed = GoogleFont("Roboto Condensed")
    private val robotoMono = GoogleFont("Roboto Mono")

    // Font Families
    val Roboto = FontFamily(
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Thin),
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Thin, style = FontStyle.Italic),
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Light),
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Light, style = FontStyle.Italic),
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Normal),
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Medium),
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Medium, style = FontStyle.Italic),
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Bold),
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Bold, style = FontStyle.Italic),
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Black),
        Font(googleFont = roboto, fontProvider = provider, weight = FontWeight.Black, style = FontStyle.Italic)
    )

    val RobotoCondensed = FontFamily(
        Font(googleFont = robotoCondensed, fontProvider = provider, weight = FontWeight.Light),
        Font(googleFont = robotoCondensed, fontProvider = provider, weight = FontWeight.Light, style = FontStyle.Italic),
        Font(googleFont = robotoCondensed, fontProvider = provider, weight = FontWeight.Normal),
        Font(googleFont = robotoCondensed, fontProvider = provider, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(googleFont = robotoCondensed, fontProvider = provider, weight = FontWeight.Medium),
        Font(googleFont = robotoCondensed, fontProvider = provider, weight = FontWeight.Medium, style = FontStyle.Italic),
        Font(googleFont = robotoCondensed, fontProvider = provider, weight = FontWeight.Bold),
        Font(googleFont = robotoCondensed, fontProvider = provider, weight = FontWeight.Bold, style = FontStyle.Italic)
    )

    val RobotoMono = FontFamily(
        Font(googleFont = robotoMono, fontProvider = provider, weight = FontWeight.Thin),
        Font(googleFont = robotoMono, fontProvider = provider, weight = FontWeight.Thin, style = FontStyle.Italic),
        Font(googleFont = robotoMono, fontProvider = provider, weight = FontWeight.Light),
        Font(googleFont = robotoMono, fontProvider = provider, weight = FontWeight.Light, style = FontStyle.Italic),
        Font(googleFont = robotoMono, fontProvider = provider, weight = FontWeight.Normal),
        Font(googleFont = robotoMono, fontProvider = provider, weight = FontWeight.Normal, style = FontStyle.Italic),
        Font(googleFont = robotoMono, fontProvider = provider, weight = FontWeight.Medium),
        Font(googleFont = robotoMono, fontProvider = provider, weight = FontWeight.Medium, style = FontStyle.Italic),
        Font(googleFont = robotoMono, fontProvider = provider, weight = FontWeight.Bold),
        Font(googleFont = robotoMono, fontProvider = provider, weight = FontWeight.Bold, style = FontStyle.Italic)
    )
}