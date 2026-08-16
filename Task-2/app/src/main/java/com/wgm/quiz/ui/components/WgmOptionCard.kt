package com.wgm.quiz.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wgm.quiz.ui.theme.*

enum class OptionState {
    NORMAL, SELECTED, CORRECT, WRONG, HIDDEN
}

@Composable
fun WgmOptionCard(
    prefix: String,
    text: String,
    state: OptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (state == OptionState.HIDDEN) {
        Spacer(modifier = modifier.height(60.dp))
        return
    }

    // ─── Animated glow pulse for SELECTED state ─────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "option_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    // ─── Scale bounce on select ─────────────────────────────────
    val scaleAnim by animateFloatAsState(
        targetValue = when (state) {
            OptionState.SELECTED -> 1.03f
            OptionState.CORRECT -> 1.02f
            OptionState.WRONG -> 0.98f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale_bounce"
    )

    val gradient = when (state) {
        OptionState.SELECTED -> WgmSelectedGradient
        OptionState.CORRECT -> WgmCorrectGradient
        OptionState.WRONG -> WgmWrongGradient
        else -> WgmNormalGradient
    }

    val borderColor = when (state) {
        OptionState.SELECTED -> WgmSelectedYellow.copy(alpha = glowAlpha)
        OptionState.CORRECT -> WgmElectricGreenStart.copy(alpha = glowAlpha)
        OptionState.WRONG -> WgmCrimsonRedStart
        else -> WgmBorderCyan
    }

    val borderWidth = when (state) {
        OptionState.SELECTED -> 3.dp
        OptionState.CORRECT -> 3.dp
        OptionState.WRONG -> 3.dp
        else -> 2.dp
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scaleAnim)
            .clip(WgmHexagonShape())
            .background(gradient)
            .border(borderWidth, borderColor, WgmHexagonShape())
            .clickable(enabled = state == OptionState.NORMAL) { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = prefix,
                color = when (state) {
                    OptionState.CORRECT, OptionState.WRONG -> Color.White
                    else -> WgmMetallicGoldStart
                },
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
    }
}
