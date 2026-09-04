package com.novachat.app.presentation.ui.util

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options

/**
 * Custom Coil [Fetcher] that allows Coil to decode and render
 * `data:image/...;base64,...` data URIs directly into Bitmaps/Drawables.
 */
class DataUriFetcher(
    private val dataUri: String,
    private val options: Options
) : Fetcher {

    override suspend fun fetch(): FetchResult {
        val base64Data = if (dataUri.contains(",")) {
            dataUri.substringAfter(",")
        } else {
            dataUri
        }
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: throw IllegalArgumentException("Failed to decode base64 bitmap")
        val drawable = BitmapDrawable(options.context.resources, bitmap)
        return DrawableResult(
            drawable = drawable,
            isSampled = false,
            dataSource = DataSource.MEMORY
        )
    }

    class Factory : Fetcher.Factory<String> {
        override fun create(data: String, options: Options, imageLoader: ImageLoader): Fetcher? {
            if (data.startsWith("data:image", ignoreCase = true)) {
                return DataUriFetcher(data, options)
            }
            return null
        }
    }
}
