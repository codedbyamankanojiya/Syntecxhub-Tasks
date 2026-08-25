package com.deshnews.app.presentation.ui.screen

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullNewsScreen(
    url: String,
    title: String,
    onBack: () -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            AndroidView(factory = { context ->
                WebView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    
                    val bgColorHex = String.format("#%06X", 0xFFFFFF and surfaceColor.toArgb())
                    val textColorHex = String.format("#%06X", 0xFFFFFF and onSurfaceColor.toArgb())

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            val script = """
                                (function() {
                                    var style = document.createElement('style');
                                    style.innerHTML = `
                                        header, footer, nav, aside, .ads, .sidebar, .comments, .social-share, .newsletter-signup { 
                                            display: none !important; 
                                        }
                                        body { 
                                            background-color: ${bgColorHex} !important; 
                                            color: ${textColorHex} !important; 
                                            font-family: sans-serif !important;
                                            padding: 16px !important;
                                        }
                                        p, h1, h2, h3, span, div { 
                                            color: ${textColorHex} !important; 
                                        }
                                        img {
                                            max-width: 100% !important;
                                            height: auto !important;
                                            border-radius: 8px !important;
                                        }
                                    `;
                                    document.head.appendChild(style);
                                })();
                            """.trimIndent()
                            view?.evaluateJavascript(script, null)
                        }
                    }
                    settings.javaScriptEnabled = true
                    loadUrl(url)
                }
            }, update = { webView ->
                webView.loadUrl(url)
            })
        }
    }
}
