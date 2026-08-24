package com.deshnews.app.presentation.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DeshNewsDarkColorScheme = darkColorScheme(
    primary              = BroadcastRed,
    onPrimary            = PureWhite,
    primaryContainer     = DarkRed,
    onPrimaryContainer   = PureWhite,
    secondary            = StudioGold,
    onSecondary          = Color(0xFF1A1200),
    secondaryContainer   = Color(0xFF78590A),
    onSecondaryContainer = StudioGold,
    tertiary             = LiveGreen,
    onTertiary           = Color(0xFF003912),
    background           = DeepNavy,
    onBackground         = PureWhite,
    surface              = CardSurface,
    onSurface            = PureWhite,
    surfaceVariant       = ElevatedSurface,
    onSurfaceVariant     = MutedText,
    surfaceTint          = BroadcastRed,
    outline              = CardBorder,
    outlineVariant       = CardBorder.copy(alpha = 0.5f),
    error                = BroadcastRed,
    onError              = PureWhite,
    scrim                = OverlayDark
)

private val DeshNewsLightColorScheme = lightColorScheme(
    primary              = BroadcastRed,
    onPrimary            = PureWhite,
    secondary            = BroadcastRed, // Use red for accents in light mode too
    onSecondary          = PureWhite,
    background           = Color(0xFFFFFFFF),
    onBackground         = DeepNavy,
    surface              = Color(0xFFF9FAFB),
    onSurface            = DeepNavy,
    surfaceVariant       = Color(0xFFF3F4F6),
    onSurfaceVariant     = SubtleText,
    outline              = Color(0xFFE5E7EB),
    error                = BroadcastRed,
    onError              = PureWhite
)

/**
 * Root Compose theme for DeshNews 24/7.
 */
@Composable
fun DeshNewsTheme(
    isDarkMode: Boolean = true,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val colorScheme = if (isDarkMode) DeshNewsDarkColorScheme else DeshNewsLightColorScheme

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor     = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars     = !isDarkMode
                isAppearanceLightNavigationBars = !isDarkMode
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = DeshNewsTypography,
        content     = content
    )
}
