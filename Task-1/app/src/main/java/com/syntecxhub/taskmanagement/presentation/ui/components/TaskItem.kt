package com.syntecxhub.taskmanagement.presentation.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.ui.graphics.Brush
import com.syntecxhub.taskmanagement.domain.model.Priority
import com.syntecxhub.taskmanagement.domain.model.Task
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun TaskItem(
    task: Task,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onDelete()
                    true
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onToggleCompletion()
                    false
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        },
        positionalThreshold = { it * 0.4f }
    )

    val progress by remember {
        derivedStateOf {
            if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                (dismissState.progress.toFloat() * 2f).coerceIn(0f, 1f)
            } else if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                (dismissState.progress.toFloat() * 2f).coerceIn(0f, 1f)
            } else 0f
        }
    }

    val isOverdue = remember(task.dueDate, task.isCompleted) {
        !task.isCompleted && task.dueDate.isBefore(LocalDateTime.now())
    }

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val direction = dismissState.dismissDirection

            val deleteBg = Brush.horizontalGradient(
                listOf(
                    MaterialTheme.colorScheme.errorContainer,
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f)
                ),
                startX = 1000f, endX = 0f
            )
            val completeBg = Brush.horizontalGradient(
                listOf(
                    MaterialTheme.colorScheme.secondaryContainer,
                    MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        when (direction) {
                            SwipeToDismissBoxValue.EndToStart -> deleteBg
                            SwipeToDismissBoxValue.StartToEnd -> completeBg
                            else -> Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                        }
                    )
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (direction == SwipeToDismissBoxValue.StartToEnd) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.scale(0.7f + progress * 0.5f)
                    ) {
                        Icon(
                            imageVector = if (task.isCompleted) Icons.Default.Undo else Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = if (task.isCompleted) "Reopen" else "Complete",
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier)
                }
                if (direction == SwipeToDismissBoxValue.EndToStart) {
                    Spacer(modifier = Modifier)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.scale(0.7f + progress * 0.5f)
                    ) {
                        Text(
                            text = "Delete",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        },
        modifier = modifier.padding(vertical = 6.dp)
    ) {
        val cardScale by animateFloatAsState(
            targetValue = 1f - progress * 0.03f,
            animationSpec = tween(100),
            label = "card_scale"
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .scale(cardScale),
            shape = RoundedCornerShape(18.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            tonalElevation = if (task.isCompleted) 0.dp else 1.dp,
            shadowElevation = if (task.priority == Priority.HIGH && !task.isCompleted) 2.dp else 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEditClick() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.size(44.dp)) {
                    CheckBoxWithHaptic(
                        checked = task.isCompleted,
                        highPriority = task.priority == Priority.HIGH,
                        onCheckedChange = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onToggleCompletion()
                        }
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (task.isCompleted) androidx.compose.ui.text.font.FontWeight.Normal else androidx.compose.ui.text.font.FontWeight.SemiBold
                            ),
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                            color = if (task.isCompleted) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (task.reminderEnabled) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Reminder set",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = if (isOverdue) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = formatDueDate(task.dueDate),
                            style = MaterialTheme.typography.labelSmall,
                            color = when {
                                task.isCompleted -> MaterialTheme.colorScheme.outline
                                isOverdue -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (isOverdue) androidx.compose.ui.text.font.FontWeight.SemiBold else null
                        )
                        if (isOverdue) {
                            Text(
                                text = "• Overdue",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                            )
                        }
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    PriorityBadge(priority = task.priority, compact = true)
                }
            }
        }
    }
}

@Composable
private fun CheckBoxWithHaptic(
    checked: Boolean,
    highPriority: Boolean,
    onCheckedChange: () -> Unit
) {
    val animated by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "checkbox_anim"
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange() }
            .background(
                brush = if (checked) {
                    Brush.linearGradient(
                        listOf(
                            if (highPriority) Color(0xFFFF6B6B) else MaterialTheme.colorScheme.secondary,
                            if (highPriority) Color(0xFFFF1744) else MaterialTheme.colorScheme.primary
                        )
                    )
                } else {
                    Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceContainerHighest,
                            MaterialTheme.colorScheme.surfaceContainerHigh
                        )
                    )
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        val outlineColor = MaterialTheme.colorScheme.outline
        androidx.compose.foundation.Canvas(modifier = Modifier.size(24.dp)) {
            val w = size.width
            val h = size.height

            if (animated < 1f) {
                drawCircle(
                    color = outlineColor.copy(alpha = 0.5f),
                    radius = (w / 2) * (1f - animated),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.2.dp.toPx())
                )
            }

            if (animated > 0.2f) {
                val checkProgress = ((animated - 0.2f) / 0.8f).coerceIn(0f, 1f)
                val path = androidx.compose.ui.graphics.Path().apply {
                    moveTo(w * 0.22f, h * 0.52f)
                    lineTo(w * 0.44f, h * 0.73f + (1f - checkProgress) * 10f)
                    lineTo(w * 0.80f - (1f - checkProgress) * 20f, h * 0.30f)
                }
                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = (animated * 1.3f).coerceIn(0f, 1f)),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 3.2f,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round
                    )
                )
            }
        }
    }
}

private fun formatDueDate(dateTime: LocalDateTime): String {
    val now = LocalDateTime.now()
    return when {
        dateTime.toLocalDate() == now.toLocalDate() ->
            "Today, ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        dateTime.toLocalDate() == now.plusDays(1).toLocalDate() ->
            "Tomorrow, ${dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}"
        dateTime.year == now.year ->
            dateTime.format(DateTimeFormatter.ofPattern("MMM d, HH:mm"))
        else ->
            dateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm"))
    }
}
