package com.novachat.app.presentation.ui.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

import com.novachat.app.presentation.ui.theme.NovaChatColors

/**
 * Interactive voice note player composable rendering a vertical amplitude
 * waveform bar chart alongside play/pause controls.
 *
 * The waveform uses a two-tone color scheme:
 * - Bars to the left of [playbackProgress] are rendered in [activeColor].
 * - Bars to the right are rendered in [inactiveColor].
 *
 * During recording, all bars animate with a breathing pulse.
 *
 * @param amplitudes       Normalized amplitude samples in range [0f..1f].
 * @param durationMs       Total duration for the "MM:SS" label.
 * @param isPlaying        True when audio is actively playing back.
 * @param isRecording      True when this widget represents live capture.
 * @param isOutgoing       Whether this is in an outgoing (sent) bubble — affects text color.
 * @param playbackProgress Playback fraction [0f..1f] — drives bar color split.
 * @param onPlayPauseClick Callback when the play/pause button is tapped.
 * @param activeColor      Color for played waveform bars.
 * @param inactiveColor    Color for unplayed waveform bars.
 * @param modifier         Compose modifier.
 */
@Composable
fun VoiceNoteWaveform(
    amplitudes: List<Float>,
    durationMs: Long,
    isPlaying: Boolean,
    isRecording: Boolean = false,
    isOutgoing: Boolean = true,
    playbackProgress: Float = 0f,
    onPlayPauseClick: () -> Unit,
    activeColor: Color = if (isOutgoing) Color.White.copy(alpha = 0.9f) else NovaChatColors.Primary,
    inactiveColor: Color = if (isOutgoing) Color.White.copy(alpha = 0.4f) else NovaChatColors.TextSecondary,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_pulse")
    val pulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val displayAmplitudes = when {
        amplitudes.isEmpty() -> List(30) { 0.3f + (it % 5) * 0.1f } // placeholder shape
        else -> amplitudes
    }

    // Downsample to max 40 bars for rendering
    val targetBars = 40
    val sampledAmplitudes = if (displayAmplitudes.size > targetBars) {
        val step = displayAmplitudes.size.toFloat() / targetBars
        (0 until targetBars).map { i ->
            val idx = (i * step).toInt().coerceIn(0, displayAmplitudes.lastIndex)
            displayAmplitudes[idx]
        }
    } else {
        displayAmplitudes
    }

    // Duration text color adapts to bubble type
    val durationTextColor = if (isOutgoing) Color.White.copy(alpha = 0.75f) else NovaChatColors.TextSecondary

    Row(
        modifier = modifier.padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // ── Play / Pause Button ───────────────────────────────────────────────────
        IconButton(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(activeColor)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = if (isOutgoing) NovaChatColors.PrimaryContainer else Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // ── Waveform Bar Chart ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .weight(1f)
                .height(36.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            sampledAmplitudes.forEachIndexed { index, amplitude ->
                val progressFraction = index.toFloat() / sampledAmplitudes.size.toFloat()
                val isPast = progressFraction <= playbackProgress

                val barColor = when {
                    isRecording -> NovaChatColors.Accent.copy(alpha = pulseAnim)
                    isPast -> activeColor
                    else -> inactiveColor
                }

                val effectiveAmplitude = if (isRecording) {
                    max(amplitude, 0.15f) * pulseAnim
                } else {
                    max(amplitude, 0.08f)
                }

                val animatedHeight by animateFloatAsState(
                    targetValue = effectiveAmplitude,
                    animationSpec = tween(durationMillis = 150),
                    label = "bar_$index"
                )

                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .fillMaxHeight(animatedHeight.coerceIn(0.08f, 1f))
                        .clip(RoundedCornerShape(2.dp))
                        .background(barColor)
                )
            }
        }

        // ── Duration Label ────────────────────────────────────────────────────────
        val displaySeconds = (durationMs / 1000).toInt()
        val mins = displaySeconds / 60
        val secs = displaySeconds % 60
        Text(
            text = "%d:%02d".format(mins, secs),
            color = durationTextColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
