package com.advice.ui.components.zoom

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/** Bitmap edge length for each PDF detail tile. */
internal const val PDF_TILE_SIZE = 512

/** Content-pixel multipliers relative to fitted page size. */
internal val PDF_TILE_LOD_LEVELS = intArrayOf(1, 2, 4, 8, 16, 32, 64)

/**
 * Smallest LOD ≥ [scale] (and ≤ [maxLod]) so tiles are never upscaled.
 * Falls back to [maxLod] when zoom exceeds the pyramid.
 */
internal fun selectTileLod(
    scale: Float,
    maxLod: Int = PDF_TILE_LOD_LEVELS.last(),
): Int {
    val cappedMax = max(1, maxLod)
    for (lod in PDF_TILE_LOD_LEVELS) {
        if (lod > cappedMax) break
        if (lod >= scale - 0.001f) return lod
    }
    return PDF_TILE_LOD_LEVELS.lastOrNull { it <= cappedMax } ?: 1
}

/**
 * Full page size in LOD pixel space: fitted content × [lod].
 */
internal fun lodPageSize(
    contentSize: Size,
    lod: Int,
): Size {
    val l = lod.coerceAtLeast(1).toFloat()
    return Size(contentSize.width * l, contentSize.height * l)
}

internal fun tileCountX(
    contentSize: Size,
    lod: Int,
    tileSize: Int = PDF_TILE_SIZE,
): Int {
    val page = lodPageSize(contentSize, lod)
    return max(1, ceil(page.width / tileSize).toInt())
}

internal fun tileCountY(
    contentSize: Size,
    lod: Int,
    tileSize: Int = PDF_TILE_SIZE,
): Int {
    val page = lodPageSize(contentSize, lod)
    return max(1, ceil(page.height / tileSize).toInt())
}

/**
 * Fitted-content-space rect for tile ([tx], [ty]) at [lod].
 */
internal fun tileContentRect(
    tx: Int,
    ty: Int,
    contentSize: Size,
    lod: Int,
    tileSize: Int = PDF_TILE_SIZE,
): ContentRegion {
    val l = lod.coerceAtLeast(1).toFloat()
    val page = lodPageSize(contentSize, lod)
    val leftPx = (tx * tileSize).toFloat().coerceAtMost(page.width)
    val topPx = (ty * tileSize).toFloat().coerceAtMost(page.height)
    val rightPx = min(page.width, leftPx + tileSize)
    val bottomPx = min(page.height, topPx + tileSize)
    return ContentRegion(
        left = leftPx / l,
        top = topPx / l,
        width = (rightPx - leftPx) / l,
        height = (bottomPx - topPx) / l,
    )
}

/**
 * Pixel size of the bitmap for tile ([tx], [ty]) at [lod] (edge tiles may be smaller).
 */
internal fun tileBitmapSize(
    tx: Int,
    ty: Int,
    contentSize: Size,
    lod: Int,
    tileSize: Int = PDF_TILE_SIZE,
): Pair<Int, Int> {
    val page = lodPageSize(contentSize, lod)
    val left = tx * tileSize
    val top = ty * tileSize
    val width = min(tileSize, ceil(page.width).toInt() - left).coerceAtLeast(1)
    val height = min(tileSize, ceil(page.height).toInt() - top).coerceAtLeast(1)
    return width to height
}

/**
 * Visible portion of the content in fitted content-space coordinates, given
 * `screen = content * scale + offset`.
 */
internal fun visibleContentRegion(
    contentSize: Size,
    viewport: Size,
    scale: Float,
    offset: Offset,
): ContentRegion? {
    if (scale <= 0f) return null
    val left = (-offset.x / scale).coerceIn(0f, contentSize.width)
    val top = (-offset.y / scale).coerceIn(0f, contentSize.height)
    val right = ((viewport.width - offset.x) / scale).coerceIn(0f, contentSize.width)
    val bottom = ((viewport.height - offset.y) / scale).coerceIn(0f, contentSize.height)
    val width = right - left
    val height = bottom - top
    if (width <= 1f || height <= 1f) return null
    return ContentRegion(left, top, width, height)
}

/**
 * Tile indices covering [region] at [lod], expanded by [prefetchRing] tiles on each side.
 */
internal fun visibleTileIndices(
    region: ContentRegion,
    contentSize: Size,
    lod: Int,
    prefetchRing: Int = 1,
    tileSize: Int = PDF_TILE_SIZE,
): List<TileCoord> {
    val l = lod.coerceAtLeast(1).toFloat()
    val maxTx = tileCountX(contentSize, lod, tileSize) - 1
    val maxTy = tileCountY(contentSize, lod, tileSize) - 1
    val leftPx = region.left * l
    val topPx = region.top * l
    val rightPx = (region.left + region.width) * l
    val bottomPx = (region.top + region.height) * l
    val minTx = (floor(leftPx / tileSize).toInt() - prefetchRing).coerceIn(0, maxTx)
    val minTy = (floor(topPx / tileSize).toInt() - prefetchRing).coerceIn(0, maxTy)
    val maxVisibleTx = (floor((rightPx - 0.001f) / tileSize).toInt() + prefetchRing).coerceIn(0, maxTx)
    val maxVisibleTy = (floor((bottomPx - 0.001f) / tileSize).toInt() + prefetchRing).coerceIn(0, maxTy)
    val out = ArrayList<TileCoord>((maxVisibleTx - minTx + 1) * (maxVisibleTy - minTy + 1))
    for (ty in minTy..maxVisibleTy) {
        for (tx in minTx..maxVisibleTx) {
            out.add(TileCoord(tx, ty))
        }
    }
    return out
}

/** Strict visible tiles with no prefetch ring. */
internal fun coveringTileIndices(
    region: ContentRegion,
    contentSize: Size,
    lod: Int,
    tileSize: Int = PDF_TILE_SIZE,
): List<TileCoord> = visibleTileIndices(region, contentSize, lod, prefetchRing = 0, tileSize = tileSize)

/**
 * Tile keys to load for the current viewport, lowest LOD first.
 * Includes every pyramid level up to [selectTileLod], with a prefetch ring only
 * on the sharpest level.
 */
internal fun buildNeededTileKeys(
    pageIndex: Int,
    contentSize: Size,
    viewport: Size,
    scale: Float,
    offset: Offset,
    maxLod: Int,
    prefetchRing: Int = 1,
): List<TileKey> {
    if (scale <= 0f || contentSize == Size.Zero || viewport == Size.Zero) return emptyList()
    val visible =
        visibleContentRegion(
            contentSize = contentSize,
            viewport = viewport,
            scale = scale,
            offset = offset,
        ) ?: return emptyList()
    val currentLod = selectTileLod(scale, maxLod)
    val out = ArrayList<TileKey>()
    val seen = HashSet<TileKey>()
    for (lod in PDF_TILE_LOD_LEVELS) {
        if (lod > currentLod || lod > maxLod) break
        val ring = if (lod == currentLod) prefetchRing else 0
        val coords =
            visibleTileIndices(
                region = visible,
                contentSize = contentSize,
                lod = lod,
                prefetchRing = ring,
            )
        for (coord in coords) {
            val key = TileKey(pageIndex, lod, coord.tx, coord.ty)
            if (seen.add(key)) out.add(key)
        }
    }
    return out
}

/**
 * True when a cached tile at [tileLod] should still be drawn under the current
 * [currentLod]. Keep every coarser level so missing sharp tiles don't fall through
 * to the soft base page (visible as half-sharp seams).
 */
internal fun shouldDrawTileLod(
    tileLod: Int,
    currentLod: Int,
): Boolean = tileLod in 1..currentLod

internal data class TileCoord(
    val tx: Int,
    val ty: Int,
)

internal data class TileKey(
    val pageIndex: Int,
    val lod: Int,
    val tx: Int,
    val ty: Int,
)

/**
 * Axis-aligned region in fitted content pixels.
 */
internal data class ContentRegion(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float,
)

/**
 * Scale/translate factors mapping PDF page points → tile bitmap pixels for [region]
 * in fitted content space.
 *
 * `bitmap = pdf * scale + translate` (independent X/Y).
 */
internal fun pdfRegionToBitmapTransform(
    pageWidth: Float,
    pageHeight: Float,
    contentSize: Size,
    region: ContentRegion,
    bitmapWidth: Int,
    bitmapHeight: Int,
): Pair<Offset, Offset> {
    val scale =
        Offset(
            x = (contentSize.width / pageWidth) * (bitmapWidth / region.width),
            y = (contentSize.height / pageHeight) * (bitmapHeight / region.height),
        )
    val translate =
        Offset(
            x = -region.left * bitmapWidth / region.width,
            y = -region.top * bitmapHeight / region.height,
        )
    return scale to translate
}
