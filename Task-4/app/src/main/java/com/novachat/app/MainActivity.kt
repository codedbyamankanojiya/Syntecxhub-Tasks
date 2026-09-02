package com.novachat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.lifecycleScope
import com.novachat.app.domain.repository.ChatRepository
import com.novachat.app.presentation.navigation.NavDestination
import com.novachat.app.presentation.navigation.NovaChatNavGraph
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Single Activity host for the NovaChat Compose UI.
 *
 * - Edge-to-edge rendering enabled via [enableEdgeToEdge].
 * - Authentication state checked at launch to determine start destination.
 * - The Compose navigation graph handles all screen transitions.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: ChatRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enable true edge-to-edge on Android 15+
        // This must be called before super.onCreate to ensure window is correctly set up
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            androidx.compose.material3.MaterialTheme {
                NovaChatNavGraph(
                    isAuthenticated = repository.isAuthenticated()
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (repository.isAuthenticated()) {
            lifecycleScope.launch {
                repository.updateUserPresence(true)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (repository.isAuthenticated()) {
            lifecycleScope.launch {
                repository.updateUserPresence(false)
            }
        }
    }
}
