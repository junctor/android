package com.advice.glitch

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.core.graphics.createBitmap
import androidx.core.graphics.scale

object Glitch {

    fun apply(canvas: Canvas, bitmap: Bitmap, isGlitch: Boolean = false) {
        val effect = ColorChannelShift(bitmap)
        effect.apply(canvas, bitmap, isGlitch)
    }

    var layout: Bitmap? = null

    private fun convertLayout(view: View): Bitmap? {
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)

        if (view.measuredWidth <= 0 || view.measuredHeight <= 0) {
            return null
        }

        val bitmap = createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        return bitmap.scale(400, 780)
    }
}
