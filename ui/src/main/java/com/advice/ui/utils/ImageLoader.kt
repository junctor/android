package com.advice.ui.utils

import android.content.Context
import coil.ImageLoader
import com.advice.core.network.Network

fun Context.getImageLoader(isPreview: Boolean = false): ImageLoader {
    val builder = ImageLoader(this)
        .newBuilder()

    val isPreviewMode = isPreview || System.getProperty("com.android.tools.idea.preview") == "true"

    if (!isPreviewMode) {
        try {
            // Using a separate function to avoid static verification issues if possible
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
