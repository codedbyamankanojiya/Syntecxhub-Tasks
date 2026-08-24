package com.deshnews.app.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Using the default system sans-serif which maps to Roboto on Android.
// For production, swap in the Inter or Outfit font family:
//   implementation("androidx.compose.ui:ui-text-google-fonts:<version>")
private val DisplaySans = FontFamily.Default

val DeshNewsTypography = Typography(
    // ── Hero / Display ─────────────────────────────────────────────
    displayLarge = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.Black,
        fontSize     = 32.sp,
        lineHeight   = 38.sp,
        letterSpacing = (-0.5).sp,
        color        = PureWhite
    ),
    displayMedium = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.ExtraBold,
        fontSize     = 26.sp,
        lineHeight   = 32.sp,
        letterSpacing = (-0.25).sp,
        color        = PureWhite
    ),
    displaySmall = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.Bold,
        fontSize     = 22.sp,
        lineHeight   = 28.sp,
        letterSpacing = 0.sp,
        color        = PureWhite
    ),

    // ── Headlines / Titles ─────────────────────────────────────────
    headlineLarge = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.ExtraBold,
        fontSize     = 20.sp,
        lineHeight   = 26.sp,
        letterSpacing = 0.sp,
        color        = PureWhite
    ),
    headlineMedium = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.Bold,
        fontSize     = 18.sp,
        lineHeight   = 24.sp,
        letterSpacing = 0.sp,
        color        = PureWhite
    ),
    headlineSmall = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.Bold,
        fontSize     = 16.sp,
        lineHeight   = 22.sp,
        letterSpacing = 0.sp,
        color        = PureWhite
    ),

    // ── Titles (cards, list items) ─────────────────────────────────
    titleLarge = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.Bold,
        fontSize     = 17.sp,
        lineHeight   = 23.sp,
        letterSpacing = 0.sp,
        color        = PureWhite
    ),
    titleMedium = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.SemiBold,
        fontSize     = 14.sp,
        lineHeight   = 20.sp,
        letterSpacing = 0.1.sp,
        color        = PureWhite
    ),
    titleSmall = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.Medium,
        fontSize     = 13.sp,
        lineHeight   = 18.sp,
        letterSpacing = 0.1.sp,
        color        = PureWhite
    ),

    // ── Body Text (article content) ────────────────────────────────
    bodyLarge = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.Normal,
        fontSize     = 16.sp,
        lineHeight   = 25.sp,
        letterSpacing = 0.15.sp,
        color        = MutedText
    ),
    bodyMedium = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.Normal,
        fontSize     = 14.sp,
        lineHeight   = 21.sp,
        letterSpacing = 0.25.sp,
        color        = MutedText
    ),
    bodySmall = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.Normal,
        fontSize     = 12.sp,
        lineHeight   = 17.sp,
        letterSpacing = 0.4.sp,
        color        = SubtleText
    ),

    // ── Labels / Chips / Badges ────────────────────────────────────
    labelLarge = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.ExtraBold,
        fontSize     = 12.sp,
        lineHeight   = 16.sp,
        letterSpacing = 0.8.sp,
        color        = PureWhite
    ),
    labelMedium = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.Bold,
        fontSize     = 11.sp,
        lineHeight   = 14.sp,
        letterSpacing = 0.6.sp,
        color        = PureWhite
    ),
    labelSmall = TextStyle(
        fontFamily   = DisplaySans,
        fontWeight   = FontWeight.Bold,
        fontSize     = 9.sp,
        lineHeight   = 12.sp,
        letterSpacing = 0.8.sp,
        color        = PureWhite
    )
)
