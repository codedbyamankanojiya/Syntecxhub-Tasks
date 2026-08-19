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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wgm.quiz.ui.theme.*
import com.wgm.quiz.viewmodel.MONEY_LADDER
import com.wgm.quiz.viewmodel.MONEY_LADDER_COINS
import com.wgm.quiz.viewmodel.SAFE_HAVENS

@Composable
fun WgmMoneyLadderScreen(
    currentLevel: Int,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E0854),
                        Color(0xFF0D0D2B)
                    )
                )
            )
            .clickable { onDismiss() } // Tap to continue
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Spacing instead of Diamond/Piggy
            Spacer(modifier = Modifier.height(40.dp))

            Spacer(modifier = Modifier.height(60.dp))

            // ── TOP JACKPOT BOX (Always ₹ 7 Crores) ─────────────────
            val jackpotAmount = MONEY_LADDER.last()
            val jackpotCoins = MONEY_LADDER_COINS.last()
            val isAtJackpot = currentLevel == 15

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(80.dp)
                    .clip(WgmHexagonShape(0.1f))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFFD700),
                                Color(0xFFB8860B)
                            )
                        )
                    )
                    .border(
                        width = if (isAtJackpot) 4.dp else 2.dp,
                        color = if (isAtJackpot) Color.White else Color(0xFFFFD700),
                        shape = WgmHexagonShape(0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = jackpotAmount,
                        color = Color(0xFF332200),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CoinIcon(size = 20.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format(java.util.Locale.US, "%,d", jackpotCoins),
                            color = Color(0xFF332200),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── LADDER LIST (₹ 1 Crore down to ₹ 1,000) ─────────────
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(0.9f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                // Show levels 14 down to 1
                val listLadder = MONEY_LADDER.dropLast(1).reversed()
                val listCoins = MONEY_LADDER_COINS.dropLast(1).reversed()
                
                itemsIndexed(listLadder) { index, amount ->
                    val level = 14 - index
                    if (level == currentLevel) {
                        CurrentLevelRow(
                            amount = amount,
                            coins = listCoins[index]
                        )
                    } else {
                        StandardLevelRow(
                            amount = amount,
                            coins = listCoins[index],
                            isPassed = level < currentLevel
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tap to Continue . .",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CoinIcon(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFFFFD700))
            .border(1.dp, Color(0xFFB8860B), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "M",
            color = Color.Black,
            fontSize = (size.value * 0.5f).sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
fun StandardLevelRow(
    amount: String,
    coins: Int,
    isPassed: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp)
            .clip(WgmHexagonShape(0.08f))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF003366).copy(alpha = if (isPassed) 0.6f else 1f),
                        Color(0xFF001F3F).copy(alpha = if (isPassed) 0.6f else 1f)
                    )
                )
            )
            .border(1.dp, Color(0xFF00BFFF).copy(alpha = 0.5f), WgmHexagonShape(0.08f)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = amount,
                color = Color.White.copy(alpha = if (isPassed) 0.6f else 1f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                CoinIcon(size = 16.dp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = String.format(java.util.Locale.US, "%,d", coins),
                    color = Color.White.copy(alpha = if (isPassed) 0.6f else 1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CurrentLevelRow(
    amount: String,
    coins: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        // Left Avatar (Single)
        AvatarWithBadge(Icons.Default.Person)

        // Highlighted Row
        Box(
            modifier = Modifier
                .weight(1f)
                .height(55.dp)
                .padding(start = 8.dp)
                .clip(WgmHexagonShape(0.08f))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4CAF50),
                            Color(0xFF2E7D32)
                        )
                    )
                )
                .border(2.dp, Color(0xFF81C784), WgmHexagonShape(0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = amount,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoinIcon(size = 18.dp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format(java.util.Locale.US, "%,d", coins),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AvatarWithBadge(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier.size(50.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, Color(0xFF00BFFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(35.dp)
            )
        }
        
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(Color(0xFF191970))
                .border(1.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.CardGiftcard,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(10.dp)
            )
        }
    }
}
