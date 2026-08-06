package com.advice.ui.components.pdf

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.ui.geometry.Size
import androidx.core.graphics.createBitmap
import com.advice.ui.components.zoom.ContentRegion
import com.advice.ui.components.zoom.pdfRegionToBitmapTransform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.Closeable
import java.io.File

internal fun Bitmap?.recycleQuietly() {
    val bmp = this ?: return
    if (!bmp.isRecycled) {
        runCatching { bmp.recycle() }
    }
}

internal class PdfRendererSession(
    file: File,
) : Closeable {
    private val fileDescriptor =
        ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    private val renderer = PdfRenderer(fileDescriptor)
    private val mutex = Mutex()
    private val pageSizes: Array<Pair<Int, Int>> =
        Array(renderer.pageCount) { index ->
            renderer.openPage(index).use { page ->
                page.width to page.height
            }
        }

    val pageCount: Int
        get() = renderer.pageCount

    fun pageAspectRatio(index: Int): Float {
        val (width, height) = pageSizes[index]
        return width.toFloat() / height.toFloat().coerceAtLeast(1f)
    }

    fun pageWidthPx(index: Int): Int = pageSizes[index].first

    suspend fun renderPage(
        index: Int,
        width: Int,
        height: Int,
    ): Bitmap =
        mutex.withLock {
            withContext(Dispatchers.Default) {
                var bitmap: Bitmap? = null
                try {
                    coroutineContext.ensureActive()
                    renderer.openPage(index).use { page ->
                        coroutineContext.ensureActive()
                        bitmap =
                            createBitmap(width, height).also { bmp ->
                                bmp.eraseColor(android.graphics.Color.WHITE)
                                coroutineContext.ensureActive()
                                page.render(
                                    bmp,
                                    null,
                                    null,
                                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
                                )
                            }
                    }
                    coroutineContext.ensureActive()
                    bitmap!!.also { bitmap = null }
                } finally {
                    bitmap.recycleQuietly()
                }
            }
        }

    /**
     * Renders [region] (in fitted content pixels) of the page into a bitmap of
     * [bitmapWidth] x [bitmapHeight], mapping the region to fill the bitmap.
     */
    suspend fun renderPageRegion(
        index: Int,
        contentSize: Size,
        region: ContentRegion,
        bitmapWidth: Int,
        bitmapHeight: Int,
    ): Bitmap =
        mutex.withLock {
            withContext(Dispatchers.Default) {
                var bitmap: Bitmap? = null
                try {
                    coroutineContext.ensureActive()
                    renderer.openPage(index).use { page ->
                        coroutineContext.ensureActive()
                        val pageWidth = page.width.toFloat()
                        val pageHeight = page.height.toFloat()
                        val (scale, translate) =
                            pdfRegionToBitmapTransform(
                                pageWidth = pageWidth,
                                pageHeight = pageHeight,
                                contentSize = contentSize,
                                region = region,
                                bitmapWidth = bitmapWidth,
                                bitmapHeight = bitmapHeight,
                            )
                        val matrix =
                            Matrix().apply {
                                setScale(scale.x, scale.y)
                                postTranslate(translate.x, translate.y)
                            }
                        bitmap =
                            createBitmap(bitmapWidth, bitmapHeight).also { bmp ->
                                bmp.eraseColor(android.graphics.Color.WHITE)
                                coroutineContext.ensureActive()
                                page.render(
                                    bmp,
                                    Rect(0, 0, bitmapWidth, bitmapHeight),
                                    matrix,
                                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
                                )
                            }
                    }
                    coroutineContext.ensureActive()
                    bitmap!!.also { bitmap = null }
                } finally {
                    bitmap.recycleQuietly()
                }
            }
        }

    override fun close() {
        runCatching { renderer.close() }
            .onFailure { Timber.w(it, "Error closing PdfRenderer") }
        runCatching { fileDescriptor.close() }
            .onFailure { Timber.w(it, "Error closing PDF file descriptor") }
    }
}
