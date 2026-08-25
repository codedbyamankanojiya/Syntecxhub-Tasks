package com.deshnews.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.deshnews.app.presentation.ui.theme.BroadcastRed
import com.deshnews.app.presentation.ui.theme.StudioGold

/**
 * Iconic dual-tone "DeshNews | 24/7" logo badge.
 *
 * Layout:
 * ┌──────────────┬────────────┐
 * │  DeshNews    │   24/7     │
 * │ (Black/Gold) │ (White/Red)│
 * └──────────────┴────────────┘
 *
 * @param modifier      Applied to the outer [Row].
 * @param cornerRadius  Rounding on the outer corners of the pill.
 * @param fontSize      Font size for both text segments.
 */
@Composable
fun BreakingNewsBadge(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 4.dp,
    fontSize: TextUnit = 11.sp
) {
    val shape = RoundedCornerShape(cornerRadius)
    Row(
        modifier          = modifier.clip(shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── "DeshNews" — Black text on Studio Gold ────────────────
        Text(
            text       = "DeshNews",
            color      = Color.Black,
            fontWeight = FontWeight.Black,
            fontSize   = fontSize,
            letterSpacing = 0.4.sp,
            modifier   = Modifier
                .background(StudioGold)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )
        // ── "24/7" — White text on Broadcast Red ──────────────────
        Text(
            text       = "24/7",
            color      = Color.White,
            fontWeight = FontWeight.Black,
            fontSize   = fontSize,
            letterSpacing = 0.6.sp,
            modifier   = Modifier
                .background(BroadcastRed)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

/**
 * Compact single-colour "BREAKING" chip — used inside headline cards
 * and as an overlay badge on photos.
 *
 * @param text     Defaults to "BREAKING"; can be overridden (e.g. "LIVE").
 * @param background Badge background colour.
 * @param textColor  Text colour (default white for contrast on coloured backgrounds).
 */
@Composable
fun BreakingChip(
    modifier: Modifier = Modifier,
    text: String = "BREAKING",
    background: Color = BroadcastRed,
    textColor: Color = Color.White,
    fontSize: TextUnit = 9.sp
) {
    Text(
        text          = text,
        color         = textColor,
        fontWeight    = FontWeight.Black,
        fontSize      = fontSize,
        letterSpacing = 0.8.sp,
        modifier      = modifier
            .clip(RoundedCornerShape(3.dp))
            .background(background)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
