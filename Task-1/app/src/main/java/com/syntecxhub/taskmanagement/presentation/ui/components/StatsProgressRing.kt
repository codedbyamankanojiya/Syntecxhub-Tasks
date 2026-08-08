package com.syntecxhub.taskmanagement.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.syntecxhub.taskmanagement.domain.model.TaskStats
import kotlin.math.roundToInt

@Composable
fun StatsProgressRing(
    stats: TaskStats,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 110.dp
) {
    val animatedPercentage by animateFloatAsState(
        targetValue = stats.completedPercentage,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ),
        label = "progress_animation"
    )

    val strokeWidth = 10.dp

    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val outlineVariant = MaterialTheme.colorScheme.outlineVariant
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size
            val halfStroke = strokeWidth.toPx() / 2f

            drawArc(
                brush = Brush.linearGradient(
                    listOf(
                        surfaceVariant,
                        outlineVariant
                    )
                ),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(halfStroke, halfStroke),
                size = Size(
                    canvasSize.width - strokeWidth.toPx(),
                    canvasSize.height - strokeWidth.toPx()
                ),
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            val sweepAngle = (animatedPercentage / 100f) * 360f
            val gradient = Brush.sweepGradient(
                listOf(
                    primary,
                    secondary,
                    tertiary,
                    primary
                )
            )

            if (sweepAngle > 0f) {
                drawArc(
                    brush = gradient,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(halfStroke, halfStroke),
                    size = Size(
                        canvasSize.width - strokeWidth.toPx(),
                        canvasSize.height - strokeWidth.toPx()
                    ),
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${animatedPercentage.roundToInt()}%",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${stats.completedTasks}/${stats.totalTasks}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StatsSummaryCard(
    stats: TaskStats,
    modifier: Modifier = Modifier,
    onExpandClick: () -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            StatsProgressRing(
                stats = stats,
                size = 96.dp
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniStatPill(
                        label = "Done",
                        count = stats.completedTasks,
                        color = MaterialTheme.colorScheme.secondary,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                    MiniStatPill(
                        label = "Open",
                        count = stats.pendingTasks,
                        color = MaterialTheme.colorScheme.tertiary,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniStatDot(
                        label = "High",
                        count = stats.highPriorityTasks,
                        dotColor = Color(0xFFE11D48)
                    )
                    MiniStatDot(
                        label = "Mid",
                        count = stats.mediumPriorityTasks,
                        dotColor = Color(0xFFF59E0B)
                    )
                    MiniStatDot(
                        label = "Low",
                        count = stats.lowPriorityTasks,
                        dotColor = Color(0xFF10B981)
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniStatPill(
    label: String,
    count: Int,
    color: Color,
    containerColor: Color
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = containerColor,
        modifier = Modifier.wrapContentSize()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )
            Text(
                text = "$count $label",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun MiniStatDot(
    label: String,
    count: Int,
    dotColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.wrapContentSize()
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(dotColor)
        )
        Text(
            text = "$label $count",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
