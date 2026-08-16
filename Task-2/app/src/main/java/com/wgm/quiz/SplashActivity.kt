package com.wgm.quiz

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.wgm.quiz.ui.theme.WgmDeepRoyalPurple
import com.wgm.quiz.ui.theme.WgmMetallicGoldStart
import com.wgm.quiz.ui.theme.WgmMetallicGoldEnd
import com.wgm.quiz.ui.theme.WgmMidnightBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        android.util.Log.d("SplashActivity", "onCreate: Splash screen starting")
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Keep the native splash visible until our Compose content is ready
        var keepSplash = true
        splashScreen.setKeepOnScreenCondition { keepSplash }

        setContent {
            LaunchedEffect(Unit) {
                keepSplash = false
            }
            SplashScreenContent(
                onAnimationFinished = {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }
            )
        }
    }
}

@Composable
fun SplashScreenContent(onAnimationFinished: () -> Unit) {
    val context = LocalContext.current

    // Load logo from assets safely in background thread
    val logoBitmap by produceState<androidx.compose.ui.graphics.ImageBitmap?>(initialValue = null) {
        value = withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                android.util.Log.d("SplashActivity", "Logo loading started")
                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                context.assets.open("Millionaire.png").use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)
                }
                
                // Calculate scale to target around 1000px to save memory while keeping quality
                options.inSampleSize = calculateInSampleSize(options, 1000, 1000)
                options.inJustDecodeBounds = false
                
                val bitmap = context.assets.open("Millionaire.png").use { stream ->
                    BitmapFactory.decodeStream(stream, null, options)?.asImageBitmap()
                }
                android.util.Log.d("SplashActivity", "Logo loading finished, success: ${bitmap != null}")
                bitmap
            } catch (t: Throwable) {
                android.util.Log.e("SplashActivity", "Failed to load logo bitmap", t)
                null
            }
        }
    }

    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    // Logo scale animation: 0.3 -> 1.0
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = tween(durationMillis = 1200, easing = EaseOutBack),
        label = "logo_scale"
    )

    // Logo alpha animation: 0 -> 1
    val alpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800, easing = EaseOut),
        label = "logo_alpha"
    )

    // Subtitle slide-up alpha
    val subtitleAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 1000, easing = EaseOut),
        label = "subtitle_alpha"
    )

    // Glow pulse on the logo
    val infiniteTransition = rememberInfiniteTransition(label = "glow_pulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2800) // Total splash duration
        android.util.Log.d("SplashActivity", "Transitioning to MainActivity")
        onAnimationFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        WgmDeepRoyalPurple,
                        WgmMidnightBlue,
                        Color(0xFF050A1A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        val logo = logoBitmap
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with scale + fade + glow
            Box(contentAlignment = Alignment.Center) {
                if (logo != null) {
                    // Glow behind logo
                    Image(
                        bitmap = logo,
                        contentDescription = null,
                        modifier = Modifier
                            .size(280.dp)
                            .scale(scale * 1.15f)
                            .alpha(glowAlpha * alpha)
                    )
                    // Main logo
                    Image(
                        bitmap = logo,
                        contentDescription = "WGM Quiz Logo",
                        modifier = Modifier
                            .size(280.dp) // Slightly larger
                            .scale(scale)
                            .alpha(alpha)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp)) // More space

            // Subtitle text
            Text(
                text = "WHO'S GONNA BE",
                color = WgmMetallicGoldStart.copy(alpha = subtitleAlpha),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "MILLIONAIRE",
                color = Color.White.copy(alpha = subtitleAlpha),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
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
