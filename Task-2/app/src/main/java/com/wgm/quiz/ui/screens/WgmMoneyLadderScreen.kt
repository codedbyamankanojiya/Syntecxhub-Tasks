package com.wgm.quiz.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wgm.quiz.ui.theme.*
import com.wgm.quiz.viewmodel.MONEY_LADDER
import com.wgm.quiz.viewmodel.SAFE_HAVENS

@Composable
fun WgmMoneyLadderScreen(
    currentLevel: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Slide-in animation
    var visible by remember { mutableStateOf(false) }
    val slideOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 400f,
        animationSpec = tween(400, easing = EaseOutCubic),
        label = "ladder_slide"
    )
    val bgAlpha by animateFloatAsState(
        targetValue = if (visible) 0.95f else 0f,
        animationSpec = tween(300),
        label = "ladder_bg_alpha"
    )

    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WgmDeepRoyalPurple.copy(alpha = bgAlpha))
            .offset(x = slideOffset.dp)
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(40.dp))

                Text(
                    text = "💰 MONEY LADDER",
                    color = WgmMetallicGoldStart,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )

                IconButton(
                    onClick = {
                        visible = false
                        onDismiss()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Text("✕", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                reverseLayout = true
            ) {
                itemsIndexed(MONEY_LADDER) { index, amount ->
                    val level = index + 1

                    // Staggered entrance
                    var itemVisible by remember { mutableStateOf(false) }
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(50L * (MONEY_LADDER.size - index))
                        itemVisible = true
                    }
                    val itemAlpha by animateFloatAsState(
                        targetValue = if (itemVisible) 1f else 0f,
                        animationSpec = tween(300),
                        label = "item_alpha_$index"
                    )

                    MoneyTierCard(
                        level = level,
                        amount = amount,
                        isCurrent = level == currentLevel,
                        isSafeHaven = SAFE_HAVENS.contains(level),
                        isPassed = level < currentLevel,
                        modifier = Modifier.alpha(itemAlpha)
                    )
                }
            }
        }
    }
}

@Composable
fun MoneyTierCard(
    level: Int,
    amount: String,
    isCurrent: Boolean,
    isSafeHaven: Boolean,
    isPassed: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Pulsing glow for current level
    val infiniteTransition = rememberInfiniteTransition(label = "tier_glow")
    val currentGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "current_glow"
    )

    val bgBrush = when {
        isCurrent -> Brush.horizontalGradient(
            colors = listOf(
                WgmElectricGreenStart.copy(alpha = currentGlow),
                WgmElectricGreenEnd.copy(alpha = currentGlow)
            )
        )
        isSafeHaven -> Brush.horizontalGradient(
            colors = listOf(
                WgmMetallicGoldStart.copy(alpha = 0.15f),
                WgmMetallicGoldEnd.copy(alpha = 0.1f)
            )
        )
        isPassed -> Brush.horizontalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.05f),
                Color.White.copy(alpha = 0.03f)
            )
        )
        else -> Brush.horizontalGradient(
            colors = listOf(Color.Transparent, Color.Transparent)
        )
    }

    val borderColor = when {
        isCurrent -> WgmElectricGreenStart
        isSafeHaven -> WgmMetallicGoldStart.copy(alpha = 0.4f)
        else -> Color.Transparent
    }

    val textColor = when {
        isCurrent -> Color.White
        isSafeHaven -> WgmMetallicGoldStart
        isPassed -> WgmDimWhite.copy(alpha = 0.5f)
        else -> Color.White.copy(alpha = 0.6f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(bgBrush, shape = RoundedCornerShape(8.dp))
            .then(
                if (borderColor != Color.Transparent)
                    Modifier.border(1.dp, borderColor, RoundedCornerShape(8.dp))
                else Modifier
            )
            .padding(vertical = 10.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Player avatar marker for current level
            if (isCurrent) {
                Text(
                    text = "▶ ",
                    color = WgmMetallicGoldStart,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
            Text(
                text = "$level",
                color = textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.width(28.dp)
            )
        }

        Text(
            text = amount,
            color = textColor,
            fontWeight = if (isCurrent || isSafeHaven) FontWeight.ExtraBold else FontWeight.Medium,
            fontSize = if (isCurrent) 18.sp else 16.sp
        )

        // Safe haven indicator
        if (isSafeHaven && !isCurrent) {
            Text(
                text = "🛡️",
                fontSize = 14.sp
            )
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}
