package com.advice.ui.components.zoom

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.max

internal const val MIN_ZOOM = 1f
internal const val DEFAULT_MAX_ZOOM = 10f
internal const val DOUBLE_TAP_ZOOM = 2.5f

/**
 * PDFs are vector, so the page's point-width is not a hard resolution limit;
 * allow zooming past it by this factor.
 */
internal const val NATIVE_ZOOM_HEADROOM = 2f

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
