package com.syntecxhub.taskmanagement

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.syntecxhub.taskmanagement.presentation.state.TaskUiEvent
import com.syntecxhub.taskmanagement.presentation.ui.screens.TaskListScreen
import com.syntecxhub.taskmanagement.presentation.ui.theme.TaskManagementTheme
import com.syntecxhub.taskmanagement.presentation.viewmodel.TaskViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        val rootView = window.decorView
        rootView.post {
            val tag = "SYNCTASK_PERM"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            TaskManagementTheme {
                val viewModel: TaskViewModel = hiltViewModel()
                TaskListScreen(viewModel = viewModel)
            }
        }
    }
}
