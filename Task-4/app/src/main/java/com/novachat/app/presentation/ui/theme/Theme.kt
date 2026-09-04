package com.novachat.app.presentation.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * NovaChat design tokens — single source of truth for every color, text style,
 * and dimension used across the app.
 *
 * Design direction: "Warm Minimal" — light backgrounds, teal primary, coral accents.
 */
object NovaChatColors {
    // -- Backgrounds
    val Background       = Color(0xFFFAF9F6)  // Warm off-white canvas
    val Surface          = Color(0xFFFFFFFF)  // Cards, top bars, input bar
    val SurfaceVariant   = Color(0xFFF0EDEA)  // Received message bubbles, chips
    val InputBackground  = Color(0xFFF5F3F0)  // Text field fill

    // -- Brand
    val Primary          = Color(0xFF1A6B5C)  // Deep teal - headers, FAB
    val PrimaryContainer = Color(0xFF1A8D7F)  // Lighter teal - sent bubbles
    val Accent           = Color(0xFFE85D4A)  // Coral - badges, errors, CTAs

    // -- Text
    val TextPrimary      = Color(0xFF1A1A1A)  // Headings, body
    val TextSecondary    = Color(0xFF7A7A7A)  // Timestamps, subtitles
    val TextOnPrimary    = Color(0xFFFFFFFF)  // Text on teal/coral backgrounds
    val TextOnSurface    = Color(0xFF1A1A1A)  // Text on white cards

    // -- Semantic
    val Online           = Color(0xFF4CAF50)  // Presence indicator
    val Divider          = Color(0xFFE8E5E0)  // List dividers
    val Unread           = Accent             // Unread badge

    // -- Misc
    val Shimmer          = Color(0xFFE0DDD8)  // Loading placeholders
    val Scrim            = Color(0x66000000)  // Bottom sheet overlay
}

object NovaChatTypography {
    val HeadlineLarge = TextStyle(
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
        color = NovaChatColors.TextPrimary
    )
    val HeadlineMedium = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.3).sp,
        color = NovaChatColors.TextPrimary
    )
    val TitleMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = NovaChatColors.TextPrimary
    )
    val BodyLarge = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 21.sp,
        color = NovaChatColors.TextPrimary
    )
    val BodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
        color = NovaChatColors.TextPrimary
    )
    val BodySmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        color = NovaChatColors.TextSecondary
    )
    val LabelMedium = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        color = NovaChatColors.TextSecondary
    )
    val LabelSmall = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.3.sp,
        color = NovaChatColors.TextSecondary
    )
}

object NovaChatDimens {
    val AvatarSmall  = 36.dp
    val AvatarMedium = 48.dp
    val AvatarLarge  = 72.dp

    val BubbleCorner       = 18.dp
    val BubbleCornerSmall  = 4.dp   // Cluster-side corner
    val BubblePaddingH     = 14.dp
    val BubblePaddingV     = 8.dp

    val ScreenPaddingH = 16.dp
    val CardElevation  = 1.dp
}
