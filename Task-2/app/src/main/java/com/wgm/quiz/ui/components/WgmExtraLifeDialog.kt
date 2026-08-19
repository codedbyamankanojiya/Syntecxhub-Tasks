package com.wgm.quiz.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wgm.quiz.ui.theme.*

/**
 * Premium Extra Life dialog with dark blurred overlay and scale-up entrance animation.
 */
@Composable
fun WgmExtraLifeDialog(
    onUseExtraLife: () -> Unit,
    onDismiss: () -> Unit
) {
    // Animated scale entrance
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.5f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "dialog_scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(300),
        label = "dialog_alpha"
    )

    LaunchedEffect(Unit) { visible = true }

    // Dark overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WgmDarkOverlay)
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        // Dialog card
        Column(
            modifier = Modifier
                .scale(scale)
                .width(320.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            WgmDeepRoyalPurple,
                            Color(0xFF0F0628)
                        )
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Lifeline icon
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = WgmMetallicGoldStart,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EXTRA LIFE",
                color = WgmMetallicGoldStart,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "You answered incorrectly!\nWould you like to use your Extra Life to stay in the game?",
                color = WgmDimWhite,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onUseExtraLife,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WgmElectricGreenStart
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "USE EXTRA LIFE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "WALK AWAY",
                    color = WgmCrimsonRedStart,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
