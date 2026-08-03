package com.advice.ui.utils

import android.content.Context
import coil.ImageLoader
import com.advice.core.network.Network
import java.util.concurrent.ConcurrentHashMap

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
        } catch (_: Throwable) {
            // Fallback to default if custom client fails
        }
    }
    return builder.build()
}

private fun applyNetworkClient(builder: ImageLoader.Builder) {
    builder.okHttpClient(Network.client)
}
