package com.advice.ui.utils

import android.content.Context
import coil.EventListener
import coil.ImageLoader
import coil.request.ErrorResult
import coil.request.ImageRequest
import com.advice.data.network.Network
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap

/**
 * Sink for image load failures, wired to Crashlytics by the app at startup. Without this,
 * failures are invisible: the UI silently swaps in the glitch logo placeholder.
 */
object ImageLoadTelemetry {
    @Volatile
    var reporter: ((url: String, error: Throwable) -> Unit)? = null
}

private val imageLoaders = ConcurrentHashMap<String, ImageLoader>()

fun Context.getImageLoader(isPreview: Boolean = false): ImageLoader {
    val isPreviewMode = isPreview || System.getProperty("com.android.tools.idea.preview") == "true"
    val key = if (isPreviewMode) "preview" else "default"
    return imageLoaders.getOrPut(key) {
        createImageLoader(applicationContext, isPreviewMode)
    }
}

private fun createImageLoader(
    context: Context,
    isPreviewMode: Boolean,
): ImageLoader {
    val builder = ImageLoader.Builder(context)
    if (!isPreviewMode) {
        try {
            applyNetworkClient(builder)
        } catch (ex: Throwable) {
            // Fallback to default if custom client fails
            Timber.e(ex, "Could not apply network client to image loader")
        }
        builder.eventListener(
            object : EventListener {
                override fun onError(
                    request: ImageRequest,
                    result: ErrorResult,
                ) {
                    ImageLoadTelemetry.reporter?.invoke(request.data.toString(), result.throwable)
                }
            },
        )
    }
    return builder.build()
}

private fun applyNetworkClient(builder: ImageLoader.Builder) {
    builder.okHttpClient(Network.client)
}
