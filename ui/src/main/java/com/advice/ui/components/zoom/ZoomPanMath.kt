package com.advice.ui.components.zoom

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

internal const val MIN_ZOOM = 1f
internal const val DEFAULT_MAX_ZOOM = 10f
internal const val DOUBLE_TAP_ZOOM = 2.5f

/**
 * PDFs are vector, so the page's point-width is not a hard resolution limit;
 * allow zooming past it by this factor.
 */
internal const val NATIVE_ZOOM_HEADROOM = 2f

/** How far pan may exceed hard offset bounds during an active gesture. */
internal const val OVERSCROLL_PX = 48f

/** Scale may briefly undershoot [MIN_ZOOM] by this factor while pinching. */
internal const val OVERSCROLL_MIN_SCALE_FACTOR = 0.9f

/** Scale may briefly overshoot [MAX_ZOOM] by this factor while pinching. */
internal const val OVERSCROLL_MAX_SCALE_FACTOR = 1.05f

/** Epsilon when picking the next double-tap zoom level. */
internal const val DOUBLE_TAP_EPSILON = 0.1f

/** Multiplicative step for on-screen +/- controls. */
internal const val ZOOM_BUTTON_STEP = 1.25f

/** Edge slack when deciding whether horizontal pan can still be consumed. */
internal const val HORIZONTAL_EDGE_EPSILON = 0.5f

/**
 * Returns the offset that keeps the content point under [centroid] fixed after scaling
 * from [oldScale] to [newScale], given transformOrigin = (0, 0):
 * `screen = content * scale + offset`.
 */
internal fun zoomAround(
    offset: Offset,
    oldScale: Float,
    newScale: Float,
    centroid: Offset,
): Offset {
    if (oldScale == 0f) return offset
    return centroid - (centroid - offset) * (newScale / oldScale)
}

/**
 * Clamps [offset] so the scaled content stays within the viewport.
 * When content is smaller than the viewport on an axis, it is centered.
 */
internal fun clampOffset(
    offset: Offset,
    scale: Float,
    viewport: Size,
    content: Size,
): Offset {
    val scaledWidth = content.width * scale
    val scaledHeight = content.height * scale
    return Offset(
        x =
            if (scaledWidth <= viewport.width) {
                (viewport.width - scaledWidth) / 2f
            } else {
                offset.x.coerceIn(viewport.width - scaledWidth, 0f)
            },
        y =
            if (scaledHeight <= viewport.height) {
                (viewport.height - scaledHeight) / 2f
            } else {
                offset.y.coerceIn(viewport.height - scaledHeight, 0f)
            },
    )
}

/**
 * Soft clamp used during an active gesture: allows [overscrollPx] past hard bounds.
 */
internal fun softClampOffset(
    offset: Offset,
    scale: Float,
    viewport: Size,
    content: Size,
    overscrollPx: Float = OVERSCROLL_PX,
): Offset {
    val (lower, upper) = offsetBounds(scale, viewport, content)
    return Offset(
        x = offset.x.coerceIn(lower.x - overscrollPx, upper.x + overscrollPx),
        y = offset.y.coerceIn(lower.y - overscrollPx, upper.y + overscrollPx),
    )
}

/**
 * Scale range allowed mid-gesture (rubber-band past hard min/max).
 */
internal fun overscrollScaleRange(maxZoom: Float): ClosedFloatingPointRange<Float> {
    val max = maxZoom.coerceAtLeast(MIN_ZOOM)
    return (MIN_ZOOM * OVERSCROLL_MIN_SCALE_FACTOR)..(max * OVERSCROLL_MAX_SCALE_FACTOR)
}

/**
 * True when [offset] sits outside the hard [offsetBounds] by more than [epsilon].
 */
internal fun isOffsetOverscrolled(
    offset: Offset,
    scale: Float,
    viewport: Size,
    content: Size,
    epsilon: Float = 0.5f,
): Boolean {
    val (lower, upper) = offsetBounds(scale, viewport, content)
    return offset.x < lower.x - epsilon ||
        offset.x > upper.x + epsilon ||
        offset.y < lower.y - epsilon ||
        offset.y > upper.y + epsilon
}

/**
 * True when [scale] is outside the hard `[MIN_ZOOM, maxZoom]` range.
 */
internal fun isScaleOverscrolled(
    scale: Float,
    maxZoom: Float,
    epsilon: Float = 0.001f,
): Boolean = scale < MIN_ZOOM - epsilon || scale > maxZoom + epsilon

/**
 * Lower/upper bounds for offset at the given [scale], suitable for
 * [androidx.compose.animation.core.Animatable.updateBounds].
 */
internal fun offsetBounds(
    scale: Float,
    viewport: Size,
    content: Size,
): Pair<Offset, Offset> {
    val scaledWidth = content.width * scale
    val scaledHeight = content.height * scale
    val minX: Float
    val maxX: Float
    if (scaledWidth <= viewport.width) {
        val centered = (viewport.width - scaledWidth) / 2f
        minX = centered
        maxX = centered
    } else {
        minX = viewport.width - scaledWidth
        maxX = 0f
    }
    val minY: Float
    val maxY: Float
    if (scaledHeight <= viewport.height) {
        val centered = (viewport.height - scaledHeight) / 2f
        minY = centered
        maxY = centered
    } else {
        minY = viewport.height - scaledHeight
        maxY = 0f
    }
    return Offset(minX, minY) to Offset(maxX, maxY)
}

/**
 * Whether the zoomed content can still absorb a horizontal pan of [deltaX]
 * (viewport space). When false at a horizontal edge, the parent pager may take
 * the excess gesture.
 *
 * [deltaX] > 0 moves content to the right (reveals left side / previous page at edge).
 * [deltaX] < 0 moves content to the left (reveals right side / next page at edge).
 */
internal fun canPanHorizontally(
    offset: Offset,
    scale: Float,
    viewport: Size,
    content: Size,
    deltaX: Float,
    edgeEpsilon: Float = HORIZONTAL_EDGE_EPSILON,
): Boolean {
    if (abs(deltaX) < 0.001f) return true
    val (lower, upper) = offsetBounds(scale, viewport, content)
    if (abs(upper.x - lower.x) < edgeEpsilon) return false
    return when {
        deltaX > 0f -> offset.x < upper.x - edgeEpsilon
        else -> offset.x > lower.x + edgeEpsilon
    }
}

/**
 * Next double-tap target: first ladder level strictly above [currentScale], else fit.
 * Levels: [MIN_ZOOM], [DOUBLE_TAP_ZOOM] (capped), [maxZoom].
 */
internal fun nextDoubleTapScale(
    currentScale: Float,
    maxZoom: Float,
    epsilon: Float = DOUBLE_TAP_EPSILON,
): Float {
    val max = maxZoom.coerceAtLeast(MIN_ZOOM)
    val mid = DOUBLE_TAP_ZOOM.coerceAtMost(max)
    val levels = listOf(MIN_ZOOM, mid, max).distinct()
    return levels.firstOrNull { it > currentScale + epsilon } ?: MIN_ZOOM
}

/**
 * Max zoom is at least [DEFAULT_MAX_ZOOM], or high enough to reach the PDF's native
 * point width (plus [NATIVE_ZOOM_HEADROOM], since vector content stays sharp past it)
 * when that exceeds the fitted width.
 */
internal fun derivedMaxZoom(
    nativePageWidthPx: Float,
    fittedWidthPx: Float,
): Float {
    if (fittedWidthPx <= 0f) return DEFAULT_MAX_ZOOM
    return max(DEFAULT_MAX_ZOOM, NATIVE_ZOOM_HEADROOM * nativePageWidthPx / fittedWidthPx)
}

/**
 * Size of content fitted inside [viewport] while preserving aspect ratio (letterboxed).
 */
internal fun fitContentSize(
    viewport: Size,
    contentAspectRatio: Float,
): Size {
    if (viewport.width <= 0f || viewport.height <= 0f || contentAspectRatio <= 0f) {
        return Size.Zero
    }
    val viewportAspect = viewport.width / viewport.height
    return if (contentAspectRatio > viewportAspect) {
        Size(viewport.width, viewport.width / contentAspectRatio)
    } else {
        Size(viewport.height * contentAspectRatio, viewport.height)
    }
}

/**
 * Adaptive base PDF render scale from device density and fitted width.
 * Targets ~1.5×–2.5× CSS pixels and caps so the longest bitmap edge stays ≤ [maxBitmapEdge].
 */
internal fun baseRenderScale(
    density: Float,
    fittedWidthPx: Float,
    maxBitmapEdge: Int = DEFAULT_MAX_BITMAP_EDGE,
    minScale: Float = 1.5f,
    maxScale: Float = 2.5f,
): Float {
    val densityBoost = (1.5f + density * 0.25f).coerceIn(minScale, maxScale)
    if (fittedWidthPx <= 0f) return minScale
    val edgeCap = maxBitmapEdge / fittedWidthPx
    return min(densityBoost, edgeCap).coerceAtLeast(1f)
}

internal const val DEFAULT_MAX_BITMAP_EDGE = 4096

/**
 * HD detail supersample ratio as a function of zoom. Monotonic in [scale], capped.
 */
internal fun detailPixelRatio(
    scale: Float,
    maxRatio: Float = 2.5f,
): Float = (1.5f + (scale - 1f).coerceIn(0f, 3f) * 0.25f).coerceAtMost(maxRatio)

/**
 * Caps width/height so `max(w, h) <= maxEdge` while preserving aspect ratio.
 */
internal fun cappedBitmapSize(
    width: Int,
    height: Int,
    maxEdge: Int = DEFAULT_MAX_BITMAP_EDGE,
): Pair<Int, Int> {
    val w = width.coerceAtLeast(1)
    val h = height.coerceAtLeast(1)
    val longest = max(w, h)
    if (longest <= maxEdge) return w to h
    val factor = maxEdge.toFloat() / longest
    return (w * factor).toInt().coerceAtLeast(1) to (h * factor).toInt().coerceAtLeast(1)
}
