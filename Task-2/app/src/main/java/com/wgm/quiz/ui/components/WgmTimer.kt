package com.wgm.quiz.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wgm.quiz.ui.theme.WgmCrimsonRedStart
import com.wgm.quiz.ui.theme.WgmMetallicGoldStart
import com.wgm.quiz.ui.theme.WgmSelectedOrange

@Composable
fun WgmTimer(
    secondsLeft: Int,
    totalSeconds: Int = 30,
    modifier: Modifier = Modifier
) {
    val progress = secondsLeft.toFloat() / totalSeconds

    // ─── Smooth animated arc transition ─────────────────────────
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 900, easing = LinearEasing),
        label = "timer_arc"
    )

    // ─── Color transition: gold → orange → red ─────────────────
    val arcColor by animateColorAsState(
        targetValue = when {
            secondsLeft > 15 -> WgmMetallicGoldStart
            secondsLeft > 5 -> WgmSelectedOrange
            else -> WgmCrimsonRedStart
        },
        animationSpec = tween(durationMillis = 500),
        label = "timer_color"
    )

    // ─── Pulsing scale when ≤ 5 seconds (urgency) ──────────────
    val infiniteTransition = rememberInfiniteTransition(label = "timer_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (secondsLeft <= 5 && secondsLeft > 0) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val textColor by animateColorAsState(
        targetValue = if (secondsLeft <= 5 && secondsLeft > 0) WgmCrimsonRedStart else Color.White,
        label = "timer_text_color"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size((80 * pulseScale).dp)
    ) {
        Canvas(modifier = Modifier.size(70.dp)) {
            // Background track
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                style = Stroke(width = 5.dp.toPx())
            )
            // Active arc
            drawArc(
                color = arcColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = secondsLeft.toString(),
            color = textColor,
            fontSize = (24 * pulseScale).sp,
            fontWeight = FontWeight.Bold
        )
    }
}
