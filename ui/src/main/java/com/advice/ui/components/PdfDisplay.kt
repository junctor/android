package com.advice.ui.components

import android.app.ActivityManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.advice.ui.components.zoom.DEFAULT_MAX_BITMAP_EDGE
import com.advice.ui.components.zoom.ZOOM_BUTTON_STEP
import com.advice.ui.components.zoom.baseRenderScale
import com.advice.ui.components.zoom.cappedBitmapSize
import com.advice.ui.components.zoom.derivedMaxZoom
import com.advice.ui.components.zoom.detailPixelRatio
import com.advice.ui.components.zoom.fitContentSize
import com.advice.ui.components.zoom.rememberZoomPanState
import com.advice.ui.components.zoom.zoomableGestures
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
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
private const val DETAIL_PIXEL_RATIO_PREVIEW = 1f
private const val DETAIL_STALE_EPSILON_PX = 8f

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
    val context = LocalContext.current
    val renderBudget = remember(context) { PdfRenderBudget.from(context) }
    val animationsEnabled = remember(context) { context.areSystemAnimationsEnabled() }

    var session by remember(file.absolutePath) { mutableStateOf<PdfRendererSession?>(null) }
    var loadFailed by remember(file.absolutePath) { mutableStateOf(false) }

    LaunchedEffect(file.absolutePath) {
        session?.close()
        session = null
        loadFailed = false
        var opened: PdfRendererSession? = null
        try {
            opened =
                withContext(Dispatchers.IO) {
                    runCatching { PdfRendererSession(file) }
                        .onFailure { Timber.e(it, "Failed to open PDF: $file") }
                        .getOrNull()
                }
            if (opened == null) {
                loadFailed = true
            } else {
                session = opened
                opened = null
            }
        } finally {
            opened?.close()
        }
    }

    DisposableEffect(file.absolutePath) {
        onDispose {
            session?.close()
            session = null
        }
    }

    val currentSession = session
    if (currentSession == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (loadFailed) {
                Text(
                    text = "Unable to open PDF",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            } else {
                ProgressSpinner()
            }
        }
        return
    }

    if (currentSession.pageCount <= 1) {
        ZoomablePdfPage(
            session = currentSession,
            pageIndex = 0,
            renderBudget = renderBudget,
            animationsEnabled = animationsEnabled,
            showZoomControls = true,
            modifier = modifier.fillMaxSize(),
        )
        return
    }

    val pagerState = rememberPagerState { currentSession.pageCount }
    // Track zoom of the current page so the pager doesn't steal mid-content pans.
    // Edge handoff: when zoomed at a horizontal edge, re-enable pager scrolling so
    // unconsumed outward pans can change pages.
    var currentPageZoomed by remember { mutableStateOf(false) }
    var currentPageAtEdge by remember { mutableStateOf(false) }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = !currentPageZoomed || currentPageAtEdge,
    ) { pageIndex ->
        val isCurrentPage = pageIndex == pagerState.currentPage
        ZoomablePdfPage(
            session = currentSession,
            pageIndex = pageIndex,
            isActive = isCurrentPage,
            renderBudget = renderBudget,
            animationsEnabled = animationsEnabled,
            showZoomControls = isCurrentPage,
            onZoomedChanged = { zoomed ->
                if (isCurrentPage) {
                    currentPageZoomed = zoomed
                }
            },
            onHorizontalEdgeChanged = { atEdge ->
                if (isCurrentPage) {
                    currentPageAtEdge = atEdge
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect {
                currentPageZoomed = false
                currentPageAtEdge = false
            }
    }
}

@Composable
private fun ZoomablePdfPage(
    session: PdfRendererSession,
    pageIndex: Int,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    renderBudget: PdfRenderBudget = PdfRenderBudget.DEFAULT,
    animationsEnabled: Boolean = true,
    showZoomControls: Boolean = false,
    onZoomedChanged: (Boolean) -> Unit = {},
    onHorizontalEdgeChanged: (Boolean) -> Unit = {},
) {
    val zoomState = rememberZoomPanState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(animationsEnabled) {
        zoomState.animationsEnabled = animationsEnabled
    }

    LaunchedEffect(zoomState.isZoomed) {
        onZoomedChanged(zoomState.isZoomed)
    }

    LaunchedEffect(zoomState.atLeftEdge, zoomState.atRightEdge, zoomState.isZoomed) {
        onHorizontalEdgeChanged(
            zoomState.isZoomed && (zoomState.atLeftEdge || zoomState.atRightEdge),
        )
    }

    LaunchedEffect(isActive) {
        if (!isActive) {
            zoomState.resetImmediate()
        }
    }

    val nestedScrollConnection =
        remember(zoomState) {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (!zoomState.isZoomed) return Offset.Zero
                    val consumeX =
                        if (zoomState.canPanHorizontally(available.x)) available.x else 0f
                    val consumeY = available.y
                    if (consumeX != 0f || consumeY != 0f) {
                        zoomState.applyTransform(
                            zoomChange = 1f,
                            panChange = Offset(consumeX, consumeY),
                            centroid = zoomState.viewportCenter(),
                            overscroll = false,
                            consumeHorizontal = consumeX != 0f,
                        )
                    }
                    return Offset(consumeX, consumeY)
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    if (!zoomState.isZoomed) return Velocity.Zero
                    // Absorb fling while zoomed so the pager does not page mid-content.
                    // Edge handoff for paging is via unconsumed drag, not fling.
                    return if (zoomState.canPanHorizontally(available.x)) {
                        available
                    } else {
                        Velocity(0f, available.y)
                    }
                }
            }
        }

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize()
                .clipToBounds()
                .background(Color.White)
                .nestedScroll(nestedScrollConnection)
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
        var hasHdDetail by remember(pageIndex) { mutableStateOf(false) }
        val hideBase = hasHdDetail && scale > DETAIL_ZOOM_THRESHOLD

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
                            alpha = if (hideBase) 0f else 1f
                        },
            ) {
                PdfPageBase(
                    session = session,
                    pageIndex = pageIndex,
                    contentWidthPx = contentSize.width,
                    contentHeightPx = contentSize.height,
                    renderBudget = renderBudget,
                )
            }

            PdfPageDetail(
                session = session,
                pageIndex = pageIndex,
                contentSize = contentSize,
                viewport = viewport,
                scale = scale,
                offset = offset,
                renderBudget = renderBudget,
                onHdDetailChanged = { hasHdDetail = it },
            )
        }

        if (showZoomControls && isActive) {
            ZoomControls(
                onZoomIn = {
                    scope.launch {
                        zoomState.animateZoomByStep(ZOOM_BUTTON_STEP)
                    }
                },
                onZoomOut = {
                    scope.launch {
                        zoomState.animateZoomByStep(1f / ZOOM_BUTTON_STEP)
                    }
                },
                onFit = {
                    scope.launch { zoomState.reset() }
                },
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
            )
        }
    }
}

@Composable
private fun ZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onFit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors =
        IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    Column(
        modifier = modifier.semantics(mergeDescendants = false) {},
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FilledIconButton(
            onClick = onZoomIn,
            modifier =
                Modifier
                    .size(48.dp)
                    .semantics { contentDescription = "Zoom in" },
            shape = CircleShape,
            colors = colors,
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
        FilledIconButton(
            onClick = onZoomOut,
            modifier =
                Modifier
                    .size(48.dp)
                    .semantics { contentDescription = "Zoom out" },
            shape = CircleShape,
            colors = colors,
        ) {
            Icon(Icons.Default.Remove, contentDescription = null)
        }
        FilledIconButton(
            onClick = onFit,
            modifier =
                Modifier
                    .size(48.dp)
                    .semantics { contentDescription = "Fit map to screen" },
            shape = CircleShape,
            colors = colors,
        ) {
            Icon(Icons.Default.ZoomOutMap, contentDescription = null)
        }
    }
}

@Composable
private fun PdfPageBase(
    session: PdfRendererSession,
    pageIndex: Int,
    contentWidthPx: Float,
    contentHeightPx: Float,
    renderBudget: PdfRenderBudget,
) {
    val density = LocalDensity.current
    val contentWidthDp = with(density) { contentWidthPx.toDp() }
    val contentHeightDp = with(density) { contentHeightPx.toDp() }
    val scale =
        baseRenderScale(
            density = density.density,
            fittedWidthPx = contentWidthPx,
            maxBitmapEdge = renderBudget.maxBitmapEdge,
            minScale = renderBudget.minBaseScale,
            maxScale = renderBudget.maxBaseScale,
        )
    val (renderWidth, renderHeight) =
        cappedBitmapSize(
            width = ceil(contentWidthPx * scale).toInt(),
            height = ceil(contentHeightPx * scale).toInt(),
            maxEdge = renderBudget.maxBitmapEdge,
        )

    var bitmap by remember(pageIndex) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(pageIndex, renderWidth, renderHeight) {
        bitmap = session.renderPage(pageIndex, renderWidth, renderHeight)
    }

    // Recycle only after Compose has dropped the previous ImageBitmap.
    DisposableEffect(bitmap) {
        val toRecycle = bitmap
        onDispose { toRecycle.recycleQuietly() }
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
 * Two-tier screen-space detail overlay: a fast 1× preview while moving, then
 * HD after settle. Drawn outside the zoom graphicsLayer so pixels map 1:1.
 */
@Composable
private fun PdfPageDetail(
    session: PdfRendererSession,
    pageIndex: Int,
    contentSize: Size,
    viewport: Size,
    scale: Float,
    offset: Offset,
    renderBudget: PdfRenderBudget,
    onHdDetailChanged: (Boolean) -> Unit,
) {
    var detail by remember(pageIndex) { mutableStateOf<Bitmap?>(null) }
    var detailIsHd by remember(pageIndex) { mutableStateOf(false) }
    var renderedFor by remember(pageIndex) {
        mutableStateOf<Pair<Float, Offset>?>(null)
    }

    LaunchedEffect(detailIsHd) {
        onHdDetailChanged(detailIsHd)
    }

    LaunchedEffect(pageIndex, contentSize, viewport, renderBudget) {
        snapshotFlow { scale to offset }
            .collectLatest { (currentScale, currentOffset) ->
                val previousFrame = renderedFor
                val movedFar =
                    previousFrame == null ||
                        absOffsetDelta(previousFrame.second, currentOffset) > DETAIL_STALE_EPSILON_PX ||
                        kotlin.math.abs(previousFrame.first - currentScale) > 0.02f

                if (movedFar) {
                    // Drop stale screen-space frames immediately.
                    detail = null
                    detailIsHd = false
                    renderedFor = null
                }

                if (currentScale < DETAIL_ZOOM_THRESHOLD ||
                    contentSize == Size.Zero ||
                    viewport == Size.Zero
                ) {
                    return@collectLatest
                }

                val visible =
                    visibleContentRect(
                        contentSize = contentSize,
                        viewport = viewport,
                        scale = currentScale,
                        offset = currentOffset,
                    ) ?: return@collectLatest

                // Fast preview at viewport resolution (no supersample).
                if (detail == null || movedFar) {
                    val preview =
                        renderDetailBitmap(
                            session = session,
                            pageIndex = pageIndex,
                            contentSize = contentSize,
                            viewport = viewport,
                            visible = visible,
                            pixelRatio = DETAIL_PIXEL_RATIO_PREVIEW,
                            maxEdge = renderBudget.maxBitmapEdge,
                        )
                    if (preview != null) {
                        detail = preview
                        detailIsHd = false
                        renderedFor = currentScale to currentOffset
                    }
                }

                delay(DETAIL_SETTLE_MS.milliseconds)

                val hdRatio =
                    detailPixelRatio(
                        scale = currentScale,
                        maxRatio = renderBudget.maxDetailPixelRatio,
                    )
                val hd =
                    renderDetailBitmap(
                        session = session,
                        pageIndex = pageIndex,
                        contentSize = contentSize,
                        viewport = viewport,
                        visible = visible,
                        pixelRatio = hdRatio,
                        maxEdge = renderBudget.maxBitmapEdge,
                    )
                if (hd != null) {
                    detail = hd
                    detailIsHd = true
                    renderedFor = currentScale to currentOffset
                }
            }
    }

    DisposableEffect(detail) {
        val toRecycle = detail
        onDispose { toRecycle.recycleQuietly() }
    }

    DisposableEffect(pageIndex) {
        onDispose {
            detailIsHd = false
            onHdDetailChanged(false)
        }
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

private suspend fun renderDetailBitmap(
    session: PdfRendererSession,
    pageIndex: Int,
    contentSize: Size,
    viewport: Size,
    visible: ContentRect,
    pixelRatio: Float,
    maxEdge: Int,
): Bitmap? {
    val (bitmapWidth, bitmapHeight) =
        cappedBitmapSize(
            width = ceil(viewport.width * pixelRatio).toInt(),
            height = ceil(viewport.height * pixelRatio).toInt(),
            maxEdge = maxEdge,
        )
    return runCatching {
        session.renderPageRegion(
            index = pageIndex,
            contentSize = contentSize,
            region = visible,
            bitmapWidth = bitmapWidth,
            bitmapHeight = bitmapHeight,
        )
    }.onFailure {
        Timber.w(it, "Detail render cancelled or failed")
    }.getOrNull()
}

private fun absOffsetDelta(
    a: Offset,
    b: Offset,
): Float = kotlin.math.max(kotlin.math.abs(a.x - b.x), kotlin.math.abs(a.y - b.y))

private fun Bitmap?.recycleQuietly() {
    val bmp = this ?: return
    if (!bmp.isRecycled) {
        runCatching { bmp.recycle() }
    }
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

/**
 * Memory / bitmap budget for PDF base + detail rendering.
 */
internal data class PdfRenderBudget(
    val maxBitmapEdge: Int,
    val minBaseScale: Float,
    val maxBaseScale: Float,
    val maxDetailPixelRatio: Float,
) {
    companion object {
        val DEFAULT =
            PdfRenderBudget(
                maxBitmapEdge = DEFAULT_MAX_BITMAP_EDGE,
                minBaseScale = 1.5f,
                maxBaseScale = 2.5f,
                maxDetailPixelRatio = 2.5f,
            )

        val LOW_RAM =
            PdfRenderBudget(
                maxBitmapEdge = 2048,
                minBaseScale = 1.25f,
                maxBaseScale = 1.75f,
                maxDetailPixelRatio = 1.5f,
            )

        fun from(context: Context): PdfRenderBudget {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val lowRam = am?.isLowRamDevice == true
            return if (lowRam) LOW_RAM else DEFAULT
        }
    }
}

private fun Context.areSystemAnimationsEnabled(): Boolean {
    val resolver = contentResolver

    fun scale(name: String): Float = runCatching { Settings.Global.getFloat(resolver, name, 1f) }.getOrDefault(1f)

    return scale(Settings.Global.ANIMATOR_DURATION_SCALE) != 0f &&
        scale(Settings.Global.TRANSITION_ANIMATION_SCALE) != 0f
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
        region: ContentRect,
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
