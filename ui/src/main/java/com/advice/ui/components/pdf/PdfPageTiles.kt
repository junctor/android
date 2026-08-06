package com.advice.ui.components.pdf

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.advice.ui.components.zoom.PDF_TILE_LOD_LEVELS
import com.advice.ui.components.zoom.PdfTileCache
import com.advice.ui.components.zoom.TileKey
import com.advice.ui.components.zoom.ZoomPanState
import com.advice.ui.components.zoom.buildNeededTileKeys
import com.advice.ui.components.zoom.coveringTileIndices
import com.advice.ui.components.zoom.selectTileLod
import com.advice.ui.components.zoom.shouldDrawTileLod
import com.advice.ui.components.zoom.tileBitmapSize
import com.advice.ui.components.zoom.tileContentRect
import com.advice.ui.components.zoom.visibleContentRegion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Content-space LOD tile overlay. Tiles sit under the same zoom graphicsLayer as the
 * base page, so they stay sharp while pinching/panning; missing tiles fill in async.
 *
 * Transform updates only refresh the *desired* tile set. A persistent worker renders
 * one tile at a time into the LRU cache and is **not** cancelled by pan/zoom
 * (`collectLatest` would discard in-flight work and force re-renders).
 */
@Composable
internal fun PdfPageTiles(
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
            val left = floor(region.left).toInt()
            val top = floor(region.top).toInt()
            val right = ceil(region.left + region.width).toInt()
            val bottom = ceil(region.top + region.height).toInt()
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
