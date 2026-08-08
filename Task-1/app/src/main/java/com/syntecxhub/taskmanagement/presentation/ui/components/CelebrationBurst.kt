package com.syntecxhub.taskmanagement.presentation.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun CelebrationBurst(
    trigger: Boolean,
    onAnimationComplete: () -> Unit,
    modifier: Modifier = Modifier,
    particleCount: Int = 80
) {
    var active by remember { mutableStateOf(false) }

    LaunchedEffect(trigger) {
        if (trigger) {
            active = true
        }
    }

    val transition = rememberInfiniteTransition(label = "celebration_transition")
    val elapsed by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "elapsed"
    )

    LaunchedEffect(active) {
        if (active) {
            kotlinx.coroutines.delay(2000)
            active = false
            onAnimationComplete()
        }
    }

    val particles = remember {
        (0 until particleCount).map {
            ParticleData(
                angle = Random.nextFloat() * 360f,
                speed = 200f + Random.nextFloat() * 400f,
                size = 4f + Random.nextFloat() * 8f,
                color = celebrationColors[Random.nextInt(celebrationColors.size)],
                delay = Random.nextFloat() * 0.15f,
                drift = (Random.nextFloat() - 0.5f) * 100f,
                gravity = 400f + Random.nextFloat() * 200f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 720f,
                shape = Random.nextInt(3)
            )
        }
    }

    if (active) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2
                val cy = size.height / 2

                particles.forEach { p ->
                    val t = (elapsed - p.delay).coerceIn(0f, 1f)
                    if (t <= 0f) return@forEach

                    val radius = p.speed * t
                    val x = cx + radius * cos(Math.toRadians(p.angle.toDouble())).toFloat() + p.drift * t
                    val baseY = cy + radius * sin(Math.toRadians(p.angle.toDouble())).toFloat()
                    val y = baseY + 0.5f * p.gravity * t * t
                    val alpha = (1f - t * 1.1f).coerceIn(0f, 1f)
                    val rotation = p.rotationSpeed * t
                    val scale = 1f - t * 0.3f

                    rotate(rotation, Offset(x, y)) {
                        val drawSize = p.size * scale
                        when (p.shape) {
                            0 -> drawCircle(
                                color = p.color.copy(alpha = alpha),
                                radius = drawSize,
                                center = Offset(x, y)
                            )
                            1 -> drawRect(
                                color = p.color.copy(alpha = alpha),
                                topLeft = Offset(x - drawSize, y - drawSize / 2),
                                size = Size(drawSize * 2f, drawSize)
                            )
                            else -> {
                                val path = androidx.compose.ui.graphics.Path().apply {
                                    moveTo(x, y - drawSize * 1.2f)
                                    lineTo(x + drawSize, y + drawSize)
                                    lineTo(x - drawSize, y + drawSize)
                                    close()
                                }
                                drawPath(
                                    path = path,
                                    color = p.color.copy(alpha = alpha)
                                )
                            }
                        }
                    }
                }
            }

            val scale by animateFloatAsState(
                targetValue = if (elapsed < 0.3f) 0.5f + elapsed * 3f else 1f - (elapsed - 0.3f) * 0.8f,
                animationSpec = tween(100),
                label = "check_scale"
            )
            val alpha by animateFloatAsState(
                targetValue = if (elapsed < 0.85f) 1f else (1f - elapsed) * 7f,
                animationSpec = tween(100),
                label = "check_alpha"
            )

            Box(
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        alpha = alpha.coerceIn(0f, 1f)
                    )
                    .size(80.dp)
                    .clipCircle()
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val path = androidx.compose.ui.graphics.Path().apply {
                        moveTo(w * 0.2f, h * 0.55f)
                        lineTo(w * 0.42f, h * 0.75f)
                        lineTo(w * 0.8f, h * 0.3f)
                    }
                    drawPath(
                        path = path,
                        color = Color.White,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 7f,
                            cap = androidx.compose.ui.graphics.StrokeCap.Round,
                            join = androidx.compose.ui.graphics.StrokeJoin.Round
                        )
                    )
                }
            }
        }
    }
}

private fun Modifier.clipCircle(): Modifier =
    this.clip(androidx.compose.foundation.shape.CircleShape)

private data class ParticleData(
    val angle: Float,
    val speed: Float,
    val size: Float,
    val color: Color,
    val delay: Float,
    val drift: Float,
    val gravity: Float,
    val rotationSpeed: Float,
    val shape: Int
)

private val celebrationColors = listOf(
    Color(0xFFFF6B6B),
    Color(0xFF4ECDC4),
    Color(0xFFFFD93D),
    Color(0xFF6BCB77),
    Color(0xFF4D96FF),
    Color(0xFFFF8FAB),
    Color(0xFFC9B1FF),
    Color(0xFFFF9F43),
    Color(0xFF38BDF8),
    Color(0xFFA855F7),
    Color(0xFFF43F5E),
    Color(0xFF06B6D4)
)
