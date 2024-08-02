package com.advice.ui.utils

import android.content.Context
import coil.ImageLoader
import com.advice.core.network.Network

fun Context.getImageLoader(): ImageLoader {
    return ImageLoader(this)
        .newBuilder()
        .okHttpClient(Network.client)
        .build()
}
