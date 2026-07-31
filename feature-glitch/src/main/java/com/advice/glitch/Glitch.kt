package com.advice.glitch

import android.graphics.Bitmap
import android.graphics.Canvas
import com.advice.glitch.effect.GlitchFrame
import com.advice.glitch.effect.GlitchRenderer
import com.advice.glitch.effect.GlitchRng

/**
 * Legacy entry point used by [GlitchContainerView]. Delegates to [GlitchRenderer].
 */
object Glitch {
    private val renderer = GlitchRenderer()
    private val rng = GlitchRng()

    fun apply(
        canvas: Canvas,
        bitmap: Bitmap,
        isGlitch: Boolean = false,
    ) {
        val frame =
            if (isGlitch) {
                rng.nextBurstFrame(bitmap.width, bitmap.height)
            } else {
                GlitchFrame.IDLE
            }
        renderer.draw(canvas, bitmap, frame, bitmap.width, bitmap.height)
    }
}
