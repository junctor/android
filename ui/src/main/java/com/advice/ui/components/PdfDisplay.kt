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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.advice.ui.components.zoom.ContentRegion
import com.advice.ui.components.zoom.DEFAULT_MAX_BITMAP_EDGE
import com.advice.ui.components.zoom.PDF_TILE_LOD_LEVELS
import com.advice.ui.components.zoom.PdfTileCache
import com.advice.ui.components.zoom.TileKey
import com.advice.ui.components.zoom.ZOOM_BUTTON_STEP
import com.advice.ui.components.zoom.ZoomPanState
import com.advice.ui.components.zoom.baseRenderScale
import com.advice.ui.components.zoom.buildNeededTileKeys
import com.advice.ui.components.zoom.cappedBitmapSize
import com.advice.ui.components.zoom.coveringTileIndices
import com.advice.ui.components.zoom.derivedMaxZoom
import com.advice.ui.components.zoom.fitContentSize
import com.advice.ui.components.zoom.pdfRegionToBitmapTransform
import com.advice.ui.components.zoom.rememberZoomPanState
import com.advice.ui.components.zoom.selectTileLod
import com.advice.ui.components.zoom.shouldDrawTileLod
import com.advice.ui.components.zoom.tileBitmapSize
import com.advice.ui.components.zoom.tileContentRect
import com.advice.ui.components.zoom.visibleContentRegion
import com.advice.ui.components.zoom.zoomableGestures
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.Closeable
import java.io.File
import kotlin.math.ceil

/** Zoom above fit where tile LODs are worth loading. */
private const val TILE_ZOOM_THRESHOLD = 1.05f

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
        var tilesCoverViewport by remember(pageIndex) { mutableStateOf(false) }
        val hideBase = tilesCoverViewport && scale > TILE_ZOOM_THRESHOLD

        // Base + tiles share one content-space graphicsLayer so pan/zoom never blanks sharpness.
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
                    renderBudget = renderBudget,
                    hidden = hideBase,
                )
                PdfPageTiles(
                    session = session,
                    pageIndex = pageIndex,
                    contentSize = contentSize,
                    viewport = viewport,
                    zoomState = zoomState,
                    renderBudget = renderBudget,
                    onCoverageChanged = { tilesCoverViewport = it },
                )
            }
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
    hidden: Boolean = false,
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
            modifier =
                Modifier
                    .size(width = contentWidthDp, height = contentHeightDp)
                    .graphicsLayer { alpha = if (hidden) 0f else 1f },
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
 * Content-space LOD tile overlay. Tiles sit under the same zoom graphicsLayer as the
 * base page, so they stay sharp while pinching/panning; missing tiles fill in async.
 *
 * Transform updates only refresh the *desired* tile set. A persistent worker renders
 * one tile at a time into the LRU cache and is **not** cancelled by pan/zoom
 * (`collectLatest` would discard in-flight work and force re-renders).
 */
@Composable
private fun PdfPageTiles(
    session: PdfRendererSession,
    pageIndex: Int,
    contentSize: Size,
    viewport: Size,
    zoomState: ZoomPanState,
    renderBudget: PdfRenderBudget,
    onCoverageChanged: (Boolean) -> Unit,
) {
    val density = LocalDensity.current
    val cache =
        remember(pageIndex, renderBudget.maxTileCacheSize) {
            PdfTileCache(renderBudget.maxTileCacheSize)
        }
    var cacheGeneration by remember(pageIndex) { mutableIntStateOf(0) }
    var covering by remember(pageIndex) { mutableStateOf(false) }

    DisposableEffect(cache) {
        onDispose { cache.clear() }
    }

    LaunchedEffect(covering) {
        onCoverageChanged(covering)
    }

    DisposableEffect(pageIndex) {
        onDispose {
            covering = false
            onCoverageChanged(false)
        }
    }

    LaunchedEffect(pageIndex, contentSize, viewport, renderBudget, zoomState) {
        val desiredKeys = MutableStateFlow<List<TileKey>>(emptyList())
        val skippedKeys = HashSet<TileKey>()

        fun refreshCoverage(
            scale: Float,
            offset: Offset,
        ) {
            if (scale < TILE_ZOOM_THRESHOLD ||
                contentSize == Size.Zero ||
                viewport == Size.Zero
            ) {
                covering = false
                return
            }
            val visible =
                visibleContentRegion(
                    contentSize = contentSize,
                    viewport = viewport,
                    scale = scale,
                    offset = offset,
                )
            if (visible == null) {
                covering = false
                return
            }
            val lod = selectTileLod(scale, renderBudget.maxLod)
            if (lod < 2) {
                covering = false
                return
            }
            val coverKeys =
                coveringTileIndices(
                    region = visible,
                    contentSize = contentSize,
                    lod = lod,
                ).map { TileKey(pageIndex, lod, it.tx, it.ty) }
            val fallbackLod =
                PDF_TILE_LOD_LEVELS.lastOrNull { it < lod && it <= renderBudget.maxLod }
            val fallbackKeys =
                if (fallbackLod != null) {
                    coveringTileIndices(
                        region = visible,
                        contentSize = contentSize,
                        lod = fallbackLod,
                    ).map { TileKey(pageIndex, fallbackLod, it.tx, it.ty) }
                } else {
                    emptyList()
                }
            covering =
                isTileSetCovering(coverKeys, cache) ||
                isTileSetCovering(fallbackKeys, cache)
        }

        // Producer: track viewport → desired keys (conflated via StateFlow).
        launch {
            snapshotFlow { zoomState.scale to zoomState.offset }
                .collect { (scale, offset) ->
                    if (scale < TILE_ZOOM_THRESHOLD ||
                        contentSize == Size.Zero ||
                        viewport == Size.Zero
                    ) {
                        desiredKeys.value = emptyList()
                        covering = false
                        return@collect
                    }
                    val keys =
                        buildNeededTileKeys(
                            pageIndex = pageIndex,
                            contentSize = contentSize,
                            viewport = viewport,
                            scale = scale,
                            offset = offset,
                            maxLod = renderBudget.maxLod,
                            prefetchRing = 1,
                        )
                    cache.touch(keys)
                    desiredKeys.value = keys
                    refreshCoverage(scale, offset)
                }
        }

        // Worker: render missing tiles lowest-LOD first; never cancel mid-tile on pan.
        launch {
            while (true) {
                val keys = desiredKeys.value
                val next =
                    keys.firstOrNull { key ->
                        key !in skippedKeys && !cache.contains(key)
                    }
                if (next == null) {
                    // Idle until the desired set includes something not yet cached.
                    desiredKeys.first { list ->
                        list.any { key -> key !in skippedKeys && !cache.contains(key) }
                    }
                    continue
                }

                val (bw, bh) =
                    tileBitmapSize(
                        tx = next.tx,
                        ty = next.ty,
                        contentSize = contentSize,
                        lod = next.lod,
                    )
                val region =
                    tileContentRect(
                        tx = next.tx,
                        ty = next.ty,
                        contentSize = contentSize,
                        lod = next.lod,
                    )
                if (region.width <= 0f || region.height <= 0f) {
                    skippedKeys.add(next)
                    continue
                }

                val bitmap =
                    try {
                        session.renderPageRegion(
                            index = pageIndex,
                            contentSize = contentSize,
                            region = region,
                            bitmapWidth = bw,
                            bitmapHeight = bh,
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Timber.w(e, "Tile render failed key=%s", next)
                        skippedKeys.add(next)
                        null
                    }

                if (bitmap != null) {
                    cache.touch(desiredKeys.value)
                    cache.put(next, bitmap)
                    cacheGeneration++
                    refreshCoverage(zoomState.scale, zoomState.offset)
                }
            }
        }
    }

    val scale = zoomState.scale
    val tiles = cacheGeneration.let { cache.snapshot() }
    if (tiles.isEmpty()) return

    val contentWidthDp = with(density) { contentSize.width.toDp() }
    val contentHeightDp = with(density) { contentSize.height.toDp() }
    val currentLod = selectTileLod(scale, renderBudget.maxLod)

    Box(modifier = Modifier.size(width = contentWidthDp, height = contentHeightDp)) {
        val ordered =
            tiles.entries
                .filter { (key, bmp) ->
                    !bmp.isRecycled && shouldDrawTileLod(key.lod, currentLod)
                }.sortedBy { it.key.lod }
        for ((key, bmp) in ordered) {
            val region = tileContentRect(key.tx, key.ty, contentSize, key.lod)
            val left = kotlin.math.floor(region.left).toInt()
            val top = kotlin.math.floor(region.top).toInt()
            val right = kotlin.math.ceil(region.left + region.width).toInt()
            val bottom = kotlin.math.ceil(region.top + region.height).toInt()
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                filterQuality = FilterQuality.High,
                modifier =
                    Modifier
                        .offset { IntOffset(left, top) }
                        .size(
                            width = with(density) { (right - left).toDp() },
                            height = with(density) { (bottom - top).toDp() },
                        ),
            )
        }
    }
}

/** True when [keys] is non-empty and every key is present in [cache]. */
private fun isTileSetCovering(
    keys: List<TileKey>,
    cache: PdfTileCache,
): Boolean = keys.isNotEmpty() && keys.all { cache.contains(it) }

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

private fun Context.areSystemAnimationsEnabled(): Boolean {
    val resolver = contentResolver

    fun scale(name: String): Float = runCatching { Settings.Global.getFloat(resolver, name, 1f) }.getOrDefault(1f)

    return scale(Settings.Global.ANIMATOR_DURATION_SCALE) != 0f &&
        scale(Settings.Global.TRANSITION_ANIMATION_SCALE) != 0f
}

private fun Bitmap?.recycleQuietly() {
    val bmp = this ?: return
    if (!bmp.isRecycled) {
        runCatching { bmp.recycle() }
    }
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
