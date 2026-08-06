package com.advice.core.utils

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * A toast message as either raw [text] or a [resId] string resource, so ViewModels
 * can push resource-based messages without holding a [Context]; the UI layer
 * resolves the final string via [resolve].
 */
data class ToastData(
    val text: String? = null,
    @StringRes val resId: Int? = null,
    val duration: Int = Toast.LENGTH_SHORT,
) {
    init {
        require(text != null || resId != null) { "ToastData requires text or resId" }
    }

    fun resolve(context: Context): String = text ?: context.getString(requireNotNull(resId))
}
