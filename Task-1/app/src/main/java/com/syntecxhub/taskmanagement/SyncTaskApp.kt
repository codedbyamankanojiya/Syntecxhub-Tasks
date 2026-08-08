package com.syntecxhub.taskmanagement

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SyncTaskApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}
