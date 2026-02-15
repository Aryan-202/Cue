package com.music.cue.org.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.music.cue.org.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val fontName = GoogleFont("Lobster Two")

val fontFamilyLobster = FontFamily(
    Font(googleFont = fontName, fontProvider = provider)
)


val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = fontFamilyLobster,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)