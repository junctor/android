package com.advice.ui.components

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.core.graphics.createBitmap
import com.advice.ui.components.zoom.derivedMaxZoom
import com.advice.ui.components.zoom.fitContentSize
import com.advice.ui.components.zoom.rememberZoomPanState
import com.advice.ui.components.zoom.zoomableGestures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.Closeable
import java.io.File
import kotlin.math.ceil
import kotlin.time.Duration.Companion.milliseconds

private const val DETAIL_SETTLE_MS = 120L
private const val DETAIL_ZOOM_THRESHOLD = 1.05f

/** Render the base page at up to this multiple of fit size for sharper mild zooms. */
private const val BASE_RENDER_SCALE = 2f

/** Supersample the settled detail overlay for sharper text/lines. */
private const val DETAIL_PIXEL_RATIO = 2f

@Composable
internal fun PdfDisplay(
    file: File,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text("PDF: ${file.name}")
        }
        return
    }

    key(file.absolutePath) {
        PdfDisplayContent(file = file, modifier = modifier)
    }
}

@Composable
private fun PdfDisplayContent(
    file: File,
    modifier: Modifier = Modifier,
) {
    val session =
        remember(file.absolutePath) {
            runCatching { PdfRendererSession(file) }
                .onFailure { Timber.e(it, "Failed to open PDF: $file") }
                .getOrNull()
        }

    DisposableEffect(session) {
        onDispose { session?.close() }
    }

    if (session == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "Unable to open PDF",
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        return
    }

    if (session.pageCount <= 1) {
        ZoomablePdfPage(
            session = session,
            pageIndex = 0,
            modifier = modifier.fillMaxSize(),
        )
        return
    }

    val pagerState = rememberPagerState { session.pageCount }
    // Track zoom of the current page so the pager doesn't steal pans.
    var currentPageZoomed by remember { mutableStateOf(false) }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = !currentPageZoomed,
    ) { pageIndex ->
        val isCurrentPage = pageIndex == pagerState.currentPage
        ZoomablePdfPage(
            session = session,
            pageIndex = pageIndex,
            isActive = isCurrentPage,
            onZoomedChanged = { zoomed ->
                if (isCurrentPage) {
                    currentPageZoomed = zoomed
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { currentPageZoomed = false }
    }
}

@Composable
private fun ZoomablePdfPage(
    session: PdfRendererSession,
    pageIndex: Int,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    onZoomedChanged: (Boolean) -> Unit = {},
) {
    val zoomState = rememberZoomPanState()

    LaunchedEffect(zoomState.isZoomed) {
        onZoomedChanged(zoomState.isZoomed)
    }

    LaunchedEffect(isActive) {
        if (!isActive) {
            zoomState.resetImmediate()
        }
    }

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize()
                .clipToBounds()
                .background(Color.White)
                .zoomableGestures(zoomState),
    ) {
        val viewport =
            Size(
                width = constraints.maxWidth.toFloat(),
                height = constraints.maxHeight.toFloat(),
            )
        val aspectRatio = session.pageAspectRatio(pageIndex)
        val contentSize = fitContentSize(viewport, aspectRatio)
        val nativeWidth = session.pageWidthPx(pageIndex).toFloat()
        val maxZoom = derivedMaxZoom(nativeWidth, contentSize.width)

        LaunchedEffect(viewport, contentSize, maxZoom) {
            if (viewport != Size.Zero && contentSize != Size.Zero) {
                zoomState.updateLayout(viewport, contentSize, maxZoom)
                zoomState.snapToLayout()
            }
        }

        val scale = zoomState.scale
        val offset = zoomState.offset

        // Base content is transformed; detail is a screen-space overlay so it is
        // never upscaled by graphicsLayer (which would blur a content-space tile).
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                            transformOrigin = TransformOrigin(0f, 0f)
                        },
            ) {
                PdfPageBase(
                    session = session,
                    pageIndex = pageIndex,
                    contentWidthPx = contentSize.width,
                    contentHeightPx = contentSize.height,
                )
            }

            PdfPageDetail(
                session = session,
                pageIndex = pageIndex,
                contentSize = contentSize,
                viewport = viewport,
                scale = scale,
                offset = offset,
            )
        }
    }
}

@Composable
private fun PdfPageBase(
    session: PdfRendererSession,
    pageIndex: Int,
    contentWidthPx: Float,
    contentHeightPx: Float,
) {
    val density = LocalDensity.current
    val contentWidthDp = with(density) { contentWidthPx.toDp() }
    val contentHeightDp = with(density) { contentHeightPx.toDp() }
    // PDF pages are vector — render above fit size so mild zoom stays sharp
    // before the settled detail overlay arrives.
    val renderWidth = ceil(contentWidthPx * BASE_RENDER_SCALE).toInt().coerceAtLeast(1)
    val renderHeight = ceil(contentHeightPx * BASE_RENDER_SCALE).toInt().coerceAtLeast(1)

    var bitmap by remember(pageIndex) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(pageIndex, renderWidth, renderHeight) {
        val rendered = session.renderPage(pageIndex, renderWidth, renderHeight)
        bitmap = rendered
    }

    DisposableEffect(pageIndex) {
        onDispose {
            bitmap = null
        }
    }

    val pageBitmap = bitmap
    if (pageBitmap != null && !pageBitmap.isRecycled) {
        Image(
            bitmap = pageBitmap.asImageBitmap(),
            contentDescription = "PDF page ${pageIndex + 1}",
            contentScale = ContentScale.FillBounds,
            filterQuality = FilterQuality.High,
            modifier = Modifier.size(width = contentWidthDp, height = contentHeightDp),
        )
    } else {
        Box(
            modifier = Modifier.size(width = contentWidthDp, height = contentHeightDp),
            contentAlignment = Alignment.Center,
        ) {
            ProgressSpinner()
        }
    }
}

/**
 * High-resolution overlay of the currently visible content rect, rendered only
 * after gestures settle. Drawn in **screen space** (outside the zoom graphicsLayer)
 * at viewport size so pixels map 1:1 to the display — avoiding the blur from
 * rasterizing a content-space tile and then upscaling it.
 */
@Composable
private fun PdfPageDetail(
    session: PdfRendererSession,
    pageIndex: Int,
    contentSize: Size,
    viewport: Size,
    scale: Float,
    offset: Offset,
) {
    var detail by remember(pageIndex) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(pageIndex, contentSize, viewport) {
        snapshotFlow { scale to offset }
            .collectLatest { (settledScale, settledOffset) ->
                // Drop the previous overlay immediately so a pan/zoom never shows
                // a stale screen-space frame in the wrong place.
                detail = null
                delay(DETAIL_SETTLE_MS.milliseconds)

                if (settledScale < DETAIL_ZOOM_THRESHOLD ||
                    contentSize == Size.Zero ||
                    viewport == Size.Zero
                ) {
                    return@collectLatest
                }

                val visible =
                    visibleContentRect(
                        contentSize = contentSize,
                        viewport = viewport,
                        scale = settledScale,
                        offset = settledOffset,
                    ) ?: return@collectLatest

                val bitmapWidth =
                    ceil(viewport.width * DETAIL_PIXEL_RATIO).toInt().coerceAtLeast(1)
                val bitmapHeight =
                    ceil(viewport.height * DETAIL_PIXEL_RATIO).toInt().coerceAtLeast(1)
                detail =
                    session.renderPageRegion(
                        index = pageIndex,
                        contentSize = contentSize,
                        region = visible,
                        bitmapWidth = bitmapWidth,
                        bitmapHeight = bitmapHeight,
                    )
            }
    }

    DisposableEffect(pageIndex) {
        onDispose { detail = null }
    }

    val pageBitmap = detail ?: return
    if (pageBitmap.isRecycled) return

    Image(
        bitmap = pageBitmap.asImageBitmap(),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        filterQuality = FilterQuality.High,
        modifier = Modifier.fillMaxSize(),
    )
}

private data class ContentRect(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float,
)

/**
 * Visible portion of the content in content-space coordinates, given
 * `screen = content * scale + offset`.
 */
private fun visibleContentRect(
    contentSize: Size,
    viewport: Size,
    scale: Float,
    offset: Offset,
): ContentRect? {
    if (scale <= 0f) return null
    val left = (-offset.x / scale).coerceIn(0f, contentSize.width)
    val top = (-offset.y / scale).coerceIn(0f, contentSize.height)
    val right = ((viewport.width - offset.x) / scale).coerceIn(0f, contentSize.width)
    val bottom = ((viewport.height - offset.y) / scale).coerceIn(0f, contentSize.height)
    val width = right - left
    val height = bottom - top
    if (width <= 1f || height <= 1f) return null
    return ContentRect(left, top, width, height)
}

private class PdfRendererSession(
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
                renderer.openPage(index).use { page ->
                    createBitmap(width, height).also { bitmap ->
                        bitmap.eraseColor(android.graphics.Color.WHITE)
                        page.render(
                            bitmap,
                            null,
                            null,
                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
                        )
                    }
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
        region: ContentRect,
        bitmapWidth: Int,
        bitmapHeight: Int,
    ): Bitmap =
        mutex.withLock {
            withContext(Dispatchers.Default) {
                renderer.openPage(index).use { page ->
                    val pageWidth = page.width.toFloat()
                    val pageHeight = page.height.toFloat()
                    val matrix =
                        Matrix().apply {
                            // PDF points -> fitted content pixels
                            postScale(
                                contentSize.width / pageWidth,
                                contentSize.height / pageHeight,
                            )
                            // Shift so region origin is at (0,0), then scale region to bitmap
                            postTranslate(-region.left, -region.top)
                            postScale(
                                bitmapWidth / region.width,
                                bitmapHeight / region.height,
                            )
                        }
                    createBitmap(bitmapWidth, bitmapHeight).also { bitmap ->
                        bitmap.eraseColor(android.graphics.Color.WHITE)
                        page.render(
                            bitmap,
                            Rect(0, 0, bitmapWidth, bitmapHeight),
                            matrix,
                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
                        )
                    }
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
