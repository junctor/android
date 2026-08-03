package com.advice.ui.glitch.effect

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import androidx.core.graphics.createBitmap
import kotlin.math.min

/**
 * Draws a [GlitchFrame] onto a canvas from a source bitmap.
 *
 * Pipeline: slice move → RGB channel split (ADD) → corruption blocks → optional scanlines.
 * Working buffers are reused across frames and only reallocated on size change.
 * Output preserves transparency (no opaque black fill) and is letterboxed into the dest.
 */
class GlitchRenderer {
    private var sliced: Bitmap? = null
    private var output: Bitmap? = null
    private var bufferWidth = 0
    private var bufferHeight = 0

    private val srcRect = Rect()
    private val dstRect = Rect()
    private val paint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val channelPaint = Paint(Paint.FILTER_BITMAP_FLAG)
    private val clearPaint =
        Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
    private val blockPaint = Paint()
    private val scanPaint = Paint()

    private val addXfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)

    private val redFilter = ColorMatrixColorFilter(ColorMatrix(channelMatrix(r = 1f)))
    private val greenFilter = ColorMatrixColorFilter(ColorMatrix(channelMatrix(g = 1f)))
    private val blueFilter = ColorMatrixColorFilter(ColorMatrix(channelMatrix(b = 1f)))

    fun draw(
        canvas: Canvas,
        source: Bitmap,
        frame: GlitchFrame,
        destWidth: Int,
        destHeight: Int,
    ) {
        if (destWidth <= 0 || destHeight <= 0) return

        if (frame.isIdle) {
            drawLetterboxed(canvas, source, destWidth, destHeight)
            return
        }

        ensureBuffers(source.width, source.height)
        val slicedBitmap = sliced ?: return
        val outputBitmap = output ?: return

        buildSliced(source, slicedBitmap, frame)
        composeChannels(slicedBitmap, outputBitmap, frame)
        drawCorruption(outputBitmap, frame)
        drawScanlines(outputBitmap, frame)

        drawLetterboxed(canvas, outputBitmap, destWidth, destHeight)
    }

    fun recycle() {
        sliced?.recycle()
        output?.recycle()
        sliced = null
        output = null
        bufferWidth = 0
        bufferHeight = 0
    }

    private fun drawLetterboxed(
        canvas: Canvas,
        bitmap: Bitmap,
        destWidth: Int,
        destHeight: Int,
    ) {
        val scale =
            min(
                destWidth.toFloat() / bitmap.width,
                destHeight.toFloat() / bitmap.height,
            )
        val drawW = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val drawH = (bitmap.height * scale).toInt().coerceAtLeast(1)
        val left = (destWidth - drawW) / 2
        val top = (destHeight - drawH) / 2
        srcRect.set(0, 0, bitmap.width, bitmap.height)
        dstRect.set(left, top, left + drawW, top + drawH)
        paint.colorFilter = null
        paint.xfermode = null
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint)
    }

    private fun ensureBuffers(
        width: Int,
        height: Int,
    ) {
        if (width == bufferWidth && height == bufferHeight && sliced != null && output != null) {
            return
        }
        sliced?.recycle()
        output?.recycle()
        sliced = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        output = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bufferWidth = width
        bufferHeight = height
    }

    private fun buildSliced(
        source: Bitmap,
        target: Bitmap,
        frame: GlitchFrame,
    ) {
        val c = Canvas(target)
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        paint.colorFilter = null
        paint.xfermode = null

        c.drawBitmap(source, 0f, 0f, paint)

        // Move bands: erase the original strip, then blit it at dx (no duplication).
        for (slice in frame.slices) {
            if (slice.height <= 0 || slice.dx == 0) continue
            val y = slice.y.coerceIn(0, source.height - 1)
            val h = slice.height.coerceAtMost(source.height - y)
            if (h <= 0) continue

            c.drawRect(
                0f,
                y.toFloat(),
                source.width.toFloat(),
                (y + h).toFloat(),
                clearPaint,
            )

            srcRect.set(0, y, source.width, y + h)
            dstRect.set(slice.dx, y, source.width + slice.dx, y + h)
            c.drawBitmap(source, srcRect, dstRect, paint)
        }
    }

    private fun composeChannels(
        slicedBitmap: Bitmap,
        outputBitmap: Bitmap,
        frame: GlitchFrame,
    ) {
        val c = Canvas(outputBitmap)
        c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val chroma = frame.chromaOffsetPx
        if (chroma == 0) {
            paint.colorFilter = null
            paint.xfermode = null
            c.drawBitmap(slicedBitmap, 0f, 0f, paint)
            return
        }

        // Red left, cyan/blue right — matches logo_glitch / DC28
        channelPaint.xfermode = addXfermode

        channelPaint.colorFilter = redFilter
        c.drawBitmap(slicedBitmap, -chroma.toFloat(), 0f, channelPaint)

        channelPaint.colorFilter = greenFilter
        c.drawBitmap(slicedBitmap, 0f, 0f, channelPaint)

        channelPaint.colorFilter = blueFilter
        c.drawBitmap(slicedBitmap, chroma.toFloat(), 0f, channelPaint)

        channelPaint.xfermode = null
        channelPaint.colorFilter = null
    }

    private fun drawCorruption(
        outputBitmap: Bitmap,
        frame: GlitchFrame,
    ) {
        if (frame.blocks.isEmpty()) return
        val c = Canvas(outputBitmap)
        for (block in frame.blocks) {
            blockPaint.color = block.color
            c.drawRect(
                block.x.toFloat(),
                block.y.toFloat(),
                (block.x + block.width).toFloat(),
                (block.y + block.height).toFloat(),
                blockPaint,
            )
        }
    }

    private fun drawScanlines(
        outputBitmap: Bitmap,
        frame: GlitchFrame,
    ) {
        val alpha = frame.scanlineAlpha.coerceIn(0, 255)
        if (alpha == 0) return
        val c = Canvas(outputBitmap)
        scanPaint.color = Color.argb(alpha, 0, 0, 0)
        val spacing = SCANLINE_SPACING
        var y = 0
        while (y < outputBitmap.height) {
            c.drawRect(0f, y.toFloat(), outputBitmap.width.toFloat(), y + 1f, scanPaint)
            y += spacing
        }
    }

    companion object {
        private const val SCANLINE_SPACING = 3

        private fun channelMatrix(
            r: Float = 0f,
            g: Float = 0f,
            b: Float = 0f,
        ): FloatArray =
            floatArrayOf(
                r,
                0f,
                0f,
                0f,
                0f,
                0f,
                g,
                0f,
                0f,
                0f,
                0f,
                0f,
                b,
                0f,
                0f,
                0f,
                0f,
                0f,
                1f,
                0f,
            )
    }
}
