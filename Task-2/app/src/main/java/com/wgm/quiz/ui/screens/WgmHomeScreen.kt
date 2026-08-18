package com.wgm.quiz.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wgm.quiz.ui.theme.*
import com.wgm.quiz.viewmodel.WgmQuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun WgmHomeScreen(viewModel: WgmQuizViewModel) {
    val context = LocalContext.current

    // Load logo from assets safely in background thread
    val logoBitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(initialValue = null) {
        value = withContext(Dispatchers.IO) {
            try {
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                context.assets.open("Millionaire.png").use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)
                }
                
                // Downsample to ~1000px to save memory while keeping quality for the logo
                options.inSampleSize = calculateInSampleSize(options, 1000, 1000)
                options.inJustDecodeBounds = false
                
                context.assets.open("Millionaire.png").use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)?.asImageBitmap()
                }
            } catch (t: Throwable) {
                android.util.Log.e("WgmHomeScreen", "Failed to load logo bitmap", t)
                null
            }
        }
    }

    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "home_anim")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )
    val buttonScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0728),
                        Color(0xFF0D1149),
                        Color(0xFF0D1135),
                        Color(0xFF070D2A)
                    )
                )
            )
    ) {
        // Radial spotlight rays
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
                .align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF1A2FCC).copy(alpha = 0.35f), Color.Transparent),
                            center = Offset(0f, 600f), radius = 900f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF1A2FCC).copy(alpha = 0.35f), Color.Transparent),
                            center = Offset(1200f, 600f), radius = 900f
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF6B4EFF).copy(alpha = glowAlpha * 0.25f), Color.Transparent),
                            center = Offset(600f, 400f), radius = 700f
                        )
                    )
            )
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            // Circular logo with animated gold glow halo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(290.dp)
            ) {
                val logo = logoBitmap
                Box(
                    modifier = Modifier
                        .size(290.dp)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    WgmGlowGold.copy(alpha = glowAlpha * 0.5f),
                                    Color(0xFFB8860B).copy(alpha = glowAlpha * 0.2f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                if (logo != null) {
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .border(4.dp, WgmGlowGold, CircleShape)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = logo,
                            contentDescription = "Who's Gonna Be Millionaire Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(1.45f),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(260.dp)
                            .background(Color(0xFF1A1060), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MILLIONAIRE",
                            color = WgmGlowGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Tagline card
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E1060).copy(alpha = 0.85f),
                                Color(0xFF160B50).copy(alpha = 0.85f)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "One Question.",
                        color = WgmGlowGold,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "One Step Closer to a Million.",
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // START QUIZ button
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .scale(buttonScale)
                    .height(62.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFE6A800),
                                Color(0xFFFFC107),
                                Color(0xFFE6A800)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { 
                        android.util.Log.d("WgmHomeScreen", "Start Quiz button clicked")
                        viewModel.startGameFromHome() 
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    elevation = ButtonDefaults.buttonElevation(0.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color(0xFF78350F).copy(alpha = 0.35f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "▶",
                                color = Color(0xFF3D1A00),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "START QUIZ",
                            color = Color(0xFF3D1A00),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Developer Credit
            Text(
                text = "Developed By Aman Kanojiya",
                color = WgmGlowGold.copy(alpha = 0.6f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
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
