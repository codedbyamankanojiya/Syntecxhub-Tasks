package com.wgm.quiz.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wgm.quiz.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Premium Audience Poll dialog with animated bar chart and dark modal overlay.
 */
@Composable
fun WgmAudiencePollDialog(
    data: Map<Int, Int>,
    options: List<String>,
    onDismiss: () -> Unit
) {
    // Animated entrance
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "poll_scale"
    )

    // Staggered bar fill animations
    val barAnimations = List(4) { index ->
        val targetPercent = data[index] ?: 0
        var animatedTarget by remember { mutableStateOf(0f) }
        val animatedValue by animateFloatAsState(
            targetValue = animatedTarget,
            animationSpec = tween(
                durationMillis = 800,
                delayMillis = index * 150,
                easing = EaseOutCubic
            ),
            label = "bar_$index"
        )
        LaunchedEffect(targetPercent) {
            animatedTarget = targetPercent.toFloat()
        }
        animatedValue
    }

    LaunchedEffect(Unit) { visible = true }

    val labels = listOf("A", "B", "C", "D")
    val maxBarHeight = 150

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WgmDarkOverlay)
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .scale(scale)
                .width(320.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(WgmDeepRoyalPurple, Color(0xFF0F0628))
                    )
                )
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊 AUDIENCE POLL",
                color = WgmMetallicGoldStart,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Bar chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(maxBarHeight.dp + 60.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                labels.forEachIndexed { i, label ->
                    val percent = barAnimations[i]
                    val barHeight = (percent / 100f * maxBarHeight).coerceAtLeast(4f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.height(maxBarHeight.dp + 60.dp)
                    ) {
                        // Percentage label
                        Text(
                            text = "${percent.toInt()}%",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Bar
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(barHeight.dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(
                                            WgmMetallicGoldStart,
                                            WgmMetallicGoldEnd
                                        )
                                    )
                                )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Label
                        Text(
                            text = label,
                            color = WgmMetallicGoldStart,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WgmDarkCyanBlueEnd
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("CONTINUE", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
