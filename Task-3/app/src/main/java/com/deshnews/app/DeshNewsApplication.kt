package com.deshnews.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point for Hilt dependency injection.
 *
 * Must be registered in [AndroidManifest.xml] via android:name=".DeshNewsApplication".
 * Hilt generates the necessary component hierarchy at compile time.
 */
@HiltAndroidApp
class DeshNewsApplication : Application()
