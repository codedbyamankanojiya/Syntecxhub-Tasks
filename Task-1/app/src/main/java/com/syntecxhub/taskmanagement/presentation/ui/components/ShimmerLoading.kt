package com.syntecxhub.taskmanagement.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerEffectBox(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 16.dp
) {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant,
        MaterialTheme.colorScheme.surfaceContainerHigh,
        MaterialTheme.colorScheme.surfaceVariant
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = 1000f, y = 1000f),
        tileMode = TileMode.Clamp
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
    )
}

@Composable
fun TaskListShimmer(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item {
            StatsCardShimmer()
        }
        items(5) {
            TaskItemShimmer()
        }
    }
}

@Composable
private fun StatsCardShimmer() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ShimmerEffectBox(
                modifier = Modifier.size(96.dp),
                cornerRadius = 48.dp
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShimmerEffectBox(
                        modifier = Modifier
                            .width(80.dp)
                            .height(28.dp),
                        cornerRadius = 14.dp
                    )
                    ShimmerEffectBox(
                        modifier = Modifier
                            .width(80.dp)
                            .height(28.dp),
                        cornerRadius = 14.dp
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ShimmerEffectBox(
                        modifier = Modifier
                            .width(60.dp)
                            .height(14.dp),
                        cornerRadius = 7.dp
                    )
                    ShimmerEffectBox(
                        modifier = Modifier
                            .width(60.dp)
                            .height(14.dp),
                        cornerRadius = 7.dp
                    )
                    ShimmerEffectBox(
                        modifier = Modifier
                            .width(60.dp)
                            .height(14.dp),
                        cornerRadius = 7.dp
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskItemShimmer() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ShimmerEffectBox(
                modifier = Modifier.size(40.dp),
                cornerRadius = 20.dp
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShimmerEffectBox(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(18.dp),
                    cornerRadius = 9.dp
                )
                ShimmerEffectBox(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(12.dp),
                    cornerRadius = 6.dp
                )
                ShimmerEffectBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(10.dp),
                    cornerRadius = 5.dp
                )
            }
            ShimmerEffectBox(
                modifier = Modifier
                    .width(60.dp)
                    .height(24.dp),
                cornerRadius = 12.dp
            )
        }
    }
}
