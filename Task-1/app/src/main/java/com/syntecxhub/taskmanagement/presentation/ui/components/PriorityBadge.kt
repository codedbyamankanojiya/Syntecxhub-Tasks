package com.syntecxhub.taskmanagement.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import com.syntecxhub.taskmanagement.domain.model.Priority

@Composable
fun PriorityBadge(
    priority: Priority,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val colors = when (priority) {
        Priority.HIGH -> PriorityColorSet(
            backgroundStart = Color(0xFFFF5252),
            backgroundEnd = Color(0xFFFF1744),
            textColor = Color.White,
            dotColor = Color(0xFFFFEB3B)
        )
        Priority.MEDIUM -> PriorityColorSet(
            backgroundStart = Color(0xFFFFAB40),
            backgroundEnd = Color(0xFFFF9100),
            textColor = Color(0xFF3E2723),
            dotColor = Color(0xFFFFF176)
        )
        Priority.LOW -> PriorityColorSet(
            backgroundStart = Color(0xFF69F0AE),
            backgroundEnd = Color(0xFF00E676),
            textColor = Color(0xFF004D40),
            dotColor = Color(0xFFB9F6CA)
        )
    }

    val animatedBg by animateColorAsState(
        targetValue = colors.backgroundStart,
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "bg_color"
    )

    val pulse = rememberInfiniteTransition(label = "priority_pulse")
    val pulseScale by pulse.animateFloat(
        initialValue = 1f,
        targetValue = if (priority == Priority.HIGH) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(if (compact) 999.dp else 10.dp))
            .background(
                brush = Brush.horizontalGradient(
                    listOf(colors.backgroundStart.copy(alpha = 0.15f), colors.backgroundEnd.copy(alpha = 0.15f))
                )
            )
            .padding(
                horizontal = if (compact) 8.dp else 10.dp,
                vertical = if (compact) 4.dp else 5.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 6.dp else 7.dp)
                .scale(pulseScale)
                .clip(RoundedCornerShape(4.dp))
                .background(colors.backgroundEnd)
        )
        Text(
            text = priority.name,
            color = colors.backgroundEnd,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = if (compact) 9.sp else 10.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

private data class PriorityColorSet(
    val backgroundStart: Color,
    val backgroundEnd: Color,
    val textColor: Color,
    val dotColor: Color
)

@Preview(showBackground = true)
@Composable
fun PriorityBadgePreview() {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PriorityBadge(priority = Priority.HIGH)
        PriorityBadge(priority = Priority.MEDIUM)
        PriorityBadge(priority = Priority.LOW)
    }
}
