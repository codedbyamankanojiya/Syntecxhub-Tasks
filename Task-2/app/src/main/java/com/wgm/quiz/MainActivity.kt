package com.wgm.quiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.wgm.quiz.ui.screens.WgmGameScreen
import com.wgm.quiz.ui.screens.WgmHomeScreen
import com.wgm.quiz.ui.theme.WgmTheme
import com.wgm.quiz.viewmodel.GamePhase
import com.wgm.quiz.viewmodel.WgmQuizViewModel
import com.wgm.quiz.viewmodel.WgmQuizViewModelFactory

class MainActivity : ComponentActivity() {

    private val app by lazy { application as WgmApplication }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WgmTheme {
                val viewModel: WgmQuizViewModel = viewModel(
                    factory = WgmQuizViewModelFactory(
                        repository = app.repository,
                        soundManager = app.soundManager,
                        scoreRepository = app.scoreRepository
                    )
                )
                val uiState by viewModel.uiState.collectAsState()

                when (uiState.gamePhase) {
                    is GamePhase.Home -> {
                        WgmHomeScreen(viewModel = viewModel)
                    }
                    else -> {
                        WgmGameScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        app.soundManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        app.soundManager.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            app.soundManager.release()
        }
    }
}
