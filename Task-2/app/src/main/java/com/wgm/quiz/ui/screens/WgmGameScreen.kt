package com.wgm.quiz.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wgm.quiz.ui.components.*
import com.wgm.quiz.ui.theme.*
import com.wgm.quiz.viewmodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun WgmGameScreen(viewModel: WgmQuizViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Load small logo from assets safely in background thread
    val logoBitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            try {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                context.assets.open("Millionaire.png").use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)
                }
                
                // Downsample more for game screen as logo is small here (48dp)
                options.inSampleSize = calculateInSampleSize(options, 500, 500)
                options.inJustDecodeBounds = false
                
                context.assets.open("Millionaire.png").use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)?.asImageBitmap()
                }
            } catch (t: Throwable) {
                android.util.Log.e("WgmGameScreen", "Failed to load logo bitmap", t)
                null
            }
        }
    }

    val logo = logoBitmap
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WgmBackgroundGradient)
    ) {
        // Background Logo Watermark
        if (logo != null) {
            Image(
                bitmap = logo,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(60.dp)
                    .alpha(0.08f),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit
            )
        }

        // ─── Main Game Content ──────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Logo + Timer + Money Ladder toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Small logo
                if (logo != null) {
                    Image(
                        bitmap = logo,
                        contentDescription = "WGM Logo",
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // Timer (centered)
                WgmTimer(secondsLeft = state.secondsLeft)

                // Money Ladder toggle
                IconButton(
                    onClick = { 
                        viewModel.playClick()
                        viewModel.toggleMoneyLadder() 
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(WgmDarkCyanBlueStart.copy(alpha = 0.6f))
                        .border(1.dp, WgmMetallicGoldStart.copy(alpha = 0.5f), CircleShape)
                ) {
                    Text(
                        text = "₹",
                        color = WgmMetallicGoldStart,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prize Badge with level
            WgmPrizeBadge(
                amountText = state.currentPrize,
                level = state.currentLevel
            )

            // Score display
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🪙 ${state.coinsEarned}",
                    color = WgmMetallicGoldStart.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "🏆 ${state.highScore}",
                    color = WgmDimWhite.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ─── Question Card with slide-up animation ──────────
            state.currentQuestion?.let { question ->
                // Animate question entrance
                var questionVisible by remember(question.id) { mutableStateOf(false) }
                LaunchedEffect(question.id) { questionVisible = true }

                val questionAlpha by animateFloatAsState(
                    targetValue = if (questionVisible) 1f else 0f,
                    animationSpec = tween(500),
                    label = "question_alpha"
                )
                val questionOffset by animateFloatAsState(
                    targetValue = if (questionVisible) 0f else 30f,
                    animationSpec = tween(500, easing = EaseOut),
                    label = "question_offset"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp)
                        .offset(y = questionOffset.dp)
                        .alpha(questionAlpha)
                        .clip(WgmHexagonShape())
                        .background(WgmNormalGradient)
                        .border(2.dp, WgmBorderCyan, WgmHexagonShape()),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = question.text,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // ─── Options with staggered entrance ────────────
                val prefixes = listOf("A:", "B:", "C:", "D:")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    question.options.forEachIndexed { index, option ->
                        // Staggered animation per option
                        var optionVisible by remember(question.id, index) { mutableStateOf(false) }
                        LaunchedEffect(question.id, index) {
                            kotlinx.coroutines.delay(100L * index)
                            optionVisible = true
                        }

                        val optionAlpha by animateFloatAsState(
                            targetValue = if (optionVisible) 1f else 0f,
                            animationSpec = tween(400),
                            label = "option_alpha_$index"
                        )

                        WgmOptionCard(
                            prefix = prefixes[index],
                            text = option,
                            state = state.optionStates[index],
                            onClick = { viewModel.onOptionSelected(index) },
                            modifier = Modifier.alpha(optionAlpha)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ─── Lifelines Bar ──────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LifelineButton(
                    label = "50:50",
                    emoji = "✂️",
                    status = state.lifelines[LifelineType.FIFTY_FIFTY] ?: LifelineStatus.AVAILABLE,
                    onClick = { 
                        viewModel.playClick()
                        viewModel.useFiftyFifty() 
                    }
                )
                LifelineButton(
                    label = "Poll",
                    emoji = "📊",
                    status = state.lifelines[LifelineType.AUDIENCE_POLL] ?: LifelineStatus.AVAILABLE,
                    onClick = { 
                        viewModel.playClick()
                        viewModel.useAudiencePoll() 
                    }
                )
                LifelineButton(
                    label = "Flip",
                    emoji = "🔄",
                    status = state.lifelines[LifelineType.FLIP] ?: LifelineStatus.AVAILABLE,
                    onClick = { 
                        viewModel.playClick()
                        viewModel.useFlip() 
                    }
                )
                LifelineButton(
                    label = "Life",
                    emoji = "❤️",
                    status = state.lifelines[LifelineType.EXTRA_LIFE] ?: LifelineStatus.AVAILABLE,
                    onClick = { 
                        viewModel.playClick()
                        /* Handled via VM logic on wrong answer */ 
                    }
                )
            }
        }

        // ─── Overlays ───────────────────────────────────────────

        // Audience Poll overlay
        if (state.showAudiencePoll) {
            WgmAudiencePollDialog(
                data = state.audiencePollData,
                options = state.currentQuestion?.options ?: listOf("A", "B", "C", "D"),
                onDismiss = { 
                    viewModel.playClick()
                    viewModel.dismissAudiencePoll() 
                }
            )
        }

        // Extra Life overlay
        if (state.showExtraLifeDialog) {
            WgmExtraLifeDialog(
                onUseExtraLife = { 
                    viewModel.playClick()
                    viewModel.useExtraLife() 
                },
                onDismiss = { 
                    viewModel.playClick()
                    viewModel.dismissExtraLife() 
                }
            )
        }

        // Money Ladder overlay
        if (state.showMoneyLadder) {
            WgmMoneyLadderScreen(
                currentLevel = state.currentLevel,
                onDismiss = { viewModel.toggleMoneyLadder() }
            )
        }

        // Game Over overlay
        if (state.gamePhase == GamePhase.GameOver || state.gamePhase == GamePhase.Victory) {
            GameOverOverlay(
                wonAmount = state.lastWonAmount,
                isVictory = state.gamePhase == GamePhase.Victory,
                totalScore = state.totalScore,
                coinsEarned = state.coinsEarned,
                highScore = state.highScore,
                onRestart = { viewModel.restartGame() },
                onExitToHome = { viewModel.goToHome() }
            )
        }

        // Loading overlay
        if (state.gamePhase == GamePhase.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WgmDarkOverlay),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = WgmMetallicGoldStart)
            }
        }
    }
}

// ─── Lifeline Button ────────────────────────────────────────────────

@Composable
fun LifelineButton(
    label: String,
    emoji: String,
    status: LifelineStatus,
    onClick: () -> Unit
) {
    val enabled = status == LifelineStatus.AVAILABLE

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(
                    if (enabled) WgmDarkCyanBlueStart.copy(alpha = 0.8f)
                    else Color.Gray.copy(alpha = 0.15f)
                )
                .border(
                    width = if (enabled) 2.dp else 1.dp,
                    color = if (enabled) WgmMetallicGoldStart else Color.Gray.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        ) {
            Text(
                text = emoji,
                fontSize = 22.sp
            )
        }
        Text(
            text = label,
            color = if (enabled) WgmMetallicGoldStart else Color.Gray.copy(alpha = 0.4f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 4.dp)
        )
        // Status indicator
        if (status == LifelineStatus.USED) {
            Text(
                text = "✗",
                color = WgmCrimsonRedStart.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─── Game Over Overlay ──────────────────────────────────────────────

@Composable
fun GameOverOverlay(
    wonAmount: String,
    isVictory: Boolean,
    totalScore: Long,
    coinsEarned: Int,
    highScore: Long,
    onRestart: () -> Unit,
    onExitToHome: () -> Unit
) {
    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(600),
        label = "gameover_alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.7f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "gameover_scale"
    )

    LaunchedEffect(Unit) { visible = true }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            WgmDeepRoyalPurple.copy(alpha = 0.95f),
                            Color(0xFF050A1A).copy(alpha = 0.98f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.scale(scale),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Title
                Text(
                    text = if (isVictory) "🎉 JACKPOT! 🎉" else "GAME OVER",
                    color = if (isVictory) WgmMetallicGoldStart else WgmCrimsonRedStart,
                    fontSize = if (isVictory) 40.sp else 44.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "YOU WON",
                    color = WgmDimWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Prize amount
                Text(
                    text = wonAmount,
                    color = WgmMetallicGoldStart,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Stats
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    StatItem(label = "Score", value = "$totalScore")
                    StatItem(label = "Coins", value = "🪙 $coinsEarned")
                    StatItem(label = "Best", value = "$highScore")
                }

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WgmMetallicGoldStart
                    ),
                    modifier = Modifier
                        .width(220.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "PLAY AGAIN",
                        color = WgmGoldText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = onExitToHome
                ) {
                    Text(
                        "Exit to Home",
                        color = WgmDimWhite.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = WgmDimWhite.copy(alpha = 0.5f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
