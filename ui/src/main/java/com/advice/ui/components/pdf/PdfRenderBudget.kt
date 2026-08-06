package com.advice.ui.components.pdf

import android.app.ActivityManager
import android.content.Context
import android.provider.Settings
import com.advice.ui.components.zoom.DEFAULT_MAX_BITMAP_EDGE

/**
 * Memory / bitmap budget for PDF base + tile rendering.
 */
internal data class PdfRenderBudget(
    val maxBitmapEdge: Int,
    val minBaseScale: Float,
    val maxBaseScale: Float,
    val maxTileCacheSize: Int,
    val maxLod: Int,
) {
    companion object {
        val DEFAULT =
            PdfRenderBudget(
                maxBitmapEdge = DEFAULT_MAX_BITMAP_EDGE,
                minBaseScale = 1.5f,
                maxBaseScale = 2.5f,
                maxTileCacheSize = 128,
                maxLod = 64,
            )

        val LOW_RAM =
            PdfRenderBudget(
                maxBitmapEdge = 2048,
                minBaseScale = 1.25f,
                maxBaseScale = 1.75f,
                maxTileCacheSize = 48,
                maxLod = 16,
            )

        fun from(context: Context): PdfRenderBudget {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val lowRam = am?.isLowRamDevice == true
            return if (lowRam) LOW_RAM else DEFAULT
        }
    }
}

internal fun Context.areSystemAnimationsEnabled(): Boolean {
    val resolver = contentResolver

    fun scale(name: String): Float = runCatching { Settings.Global.getFloat(resolver, name, 1f) }.getOrDefault(1f)

    return scale(Settings.Global.ANIMATOR_DURATION_SCALE) != 0f &&
        scale(Settings.Global.TRANSITION_ANIMATION_SCALE) != 0f
}
