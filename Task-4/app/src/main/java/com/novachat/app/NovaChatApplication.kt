package com.novachat.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.novachat.app.presentation.ui.util.DataUriFetcher
import dagger.hilt.android.HiltAndroidApp

/**
 * NovaChat Application class.
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation
 * and initialize the Dagger component hierarchy at app startup.
 */
@HiltAndroidApp
class NovaChatApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(DataUriFetcher.Factory())
            }
            .crossfade(true)
            .build()
    }
}

