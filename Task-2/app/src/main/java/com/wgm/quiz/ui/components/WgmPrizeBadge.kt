package com.wgm.quiz.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wgm.quiz.ui.theme.WgmGoldText
import com.wgm.quiz.ui.theme.WgmHexagonShape
import com.wgm.quiz.ui.theme.WgmMetallicGoldEnd
import com.wgm.quiz.ui.theme.WgmMetallicGoldStart
import com.wgm.quiz.ui.theme.WgmPrizeGradient

@Composable
fun WgmPrizeBadge(
    amountText: String,
    level: Int = 0,
    modifier: Modifier = Modifier
) {
    // ─── Shimmer animation ──────────────────────────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_x"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            WgmMetallicGoldStart,
            Color.White.copy(alpha = 0.8f),
            WgmMetallicGoldEnd
        ),
        start = Offset(shimmerX, 0f),
        end = Offset(shimmerX + 200f, 0f)
    )

    Box(
        modifier = modifier
            .width(240.dp)
            .height(44.dp)
            .clip(WgmHexagonShape(0.1f))
            .background(WgmPrizeGradient),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (level > 0) {
                Text(
                    text = "Q$level • ",
                    color = WgmGoldText.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Text(
                text = amountText,
                style = androidx.compose.ui.text.TextStyle(
                    brush = shimmerBrush,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            )
        }
    }
}
