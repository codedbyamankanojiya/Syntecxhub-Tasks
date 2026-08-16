package com.wgm.quiz.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ─── Core Millionaire Theme Colors ──────────────────────────────────
val WgmDeepRoyalPurple = Color(0xFF1A0B36)
val WgmMidnightBlue = Color(0xFF0D1335)
val WgmDarkCyanBlueStart = Color(0xFF032B45)
val WgmDarkCyanBlueEnd = Color(0xFF004F7A)
val WgmElectricGreenStart = Color(0xFF22C55E)
val WgmElectricGreenEnd = Color(0xFF15803D)
val WgmCrimsonRedStart = Color(0xFFEF4444)
val WgmCrimsonRedEnd = Color(0xFF991B1B)
val WgmMetallicGoldStart = Color(0xFFFACC15)
val WgmMetallicGoldEnd = Color(0xFFCA8A04)
val WgmGoldText = Color(0xFF78350F)
val WgmBorderCyan = Color(0xFF00E5FF)

// ─── New Enhanced Colors ────────────────────────────────────────────
val WgmSelectedOrange = Color(0xFFFF9800)
val WgmSelectedYellow = Color(0xFFFFEB3B)
val WgmGlowCyan = Color(0xFF00BCD4)
val WgmGlowGold = Color(0xFFFFD700)
val WgmDimWhite = Color(0xFFB0BEC5)
val WgmDarkOverlay = Color(0xCC000000) // 80% black overlay

// ─── Background Gradients ───────────────────────────────────────────
val WgmBackgroundGradient = Brush.verticalGradient(
    colors = listOf(WgmDeepRoyalPurple, WgmMidnightBlue)
)

val WgmNormalGradient = Brush.verticalGradient(
    colors = listOf(WgmDarkCyanBlueStart, WgmDarkCyanBlueEnd)
)

val WgmCorrectGradient = Brush.verticalGradient(
    colors = listOf(WgmElectricGreenStart, WgmElectricGreenEnd)
)

val WgmWrongGradient = Brush.verticalGradient(
    colors = listOf(WgmCrimsonRedStart, WgmCrimsonRedEnd)
)

val WgmPrizeGradient = Brush.horizontalGradient(
    colors = listOf(WgmMetallicGoldStart, WgmMetallicGoldEnd)
)

// ─── New State-Specific Gradients ───────────────────────────────────
val WgmSelectedGradient = Brush.verticalGradient(
    colors = listOf(WgmSelectedOrange, Color(0xFFE65100))
)

val WgmCorrectGlowGradient = Brush.radialGradient(
    colors = listOf(WgmElectricGreenStart.copy(alpha = 0.4f), Color.Transparent)
)

val WgmWrongGlowGradient = Brush.radialGradient(
    colors = listOf(WgmCrimsonRedStart.copy(alpha = 0.4f), Color.Transparent)
)

@Composable
fun WgmTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = darkColorScheme(
        primary = WgmMetallicGoldStart,
        secondary = WgmDarkCyanBlueEnd,
        background = WgmMidnightBlue,
        surface = WgmDeepRoyalPurple,
        onPrimary = WgmGoldText,
        onSecondary = Color.White
    )

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
