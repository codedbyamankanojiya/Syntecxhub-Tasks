package com.deshnews.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.deshnews.app.presentation.ui.screen.NewsDetailScreen
import com.deshnews.app.presentation.ui.screen.NewsHomeScreen
import com.deshnews.app.presentation.ui.theme.DeshNewsTheme
import com.deshnews.app.presentation.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * Single-Activity host for DeshNews 24/7.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NewsViewModel = hiltViewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()

            DeshNewsTheme(isDarkMode = isDarkMode) {
                val navController = rememberNavController()

                NavHost(
                    navController  = navController,
                    startDestination = Screen.Home.route,
                    modifier       = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Home.route) {
                        NewsHomeScreen(
                            viewModel = viewModel,
                            onArticleClick = { articleUrl ->
                                val encoded = URLEncoder.encode(
                                    articleUrl,
                                    StandardCharsets.UTF_8.toString()
                                )
                                navController.navigate(Screen.Detail.createRoute(encoded))
                            }
                        )
                    }

                    composable(
                        route     = Screen.Detail.route,
                        arguments = listOf(
                            navArgument("articleUrl") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val encoded    = backStackEntry.arguments?.getString("articleUrl") ?: ""
                        val articleUrl = URLDecoder.decode(encoded, StandardCharsets.UTF_8.toString())
                        NewsDetailScreen(
                            articleUrl = articleUrl,
                            onBack     = { navController.popBackStack() },
                            onArticleClick = { nextUrl ->
                                val encodedNext = URLEncoder.encode(
                                    nextUrl,
                                    StandardCharsets.UTF_8.toString()
                                )
                                navController.navigate(Screen.Detail.createRoute(encodedNext))
                            }
                        )
                    }
                }
            }
        }
    }
}

/** Type-safe navigation route definitions. */
sealed class Screen(val route: String) {
    object Home   : Screen("home")
    object Detail : Screen("detail/{articleUrl}") {
        fun createRoute(encodedUrl: String) = "detail/$encodedUrl"
    }
}
