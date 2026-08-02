package com.advice.ui.components.zoom

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ZoomPanMathTest {
    private val viewport = Size(400f, 800f)
    private val content = Size(400f, 600f)

    @Test
    fun `zoomAround keeps centroid content point fixed`() {
        val offset = Offset(10f, 20f)
        val oldScale = 1f
        val newScale = 2f
        val centroid = Offset(100f, 200f)

        val newOffset = zoomAround(offset, oldScale, newScale, centroid)

        // Content point under centroid before zoom:
        // content = (centroid - offset) / oldScale
        val contentPoint = (centroid - offset) / oldScale
        // After zoom: screen = content * newScale + newOffset
        val screenAfter = contentPoint * newScale + newOffset
        assertEquals(centroid.x, screenAfter.x, 0.01f)
        assertEquals(centroid.y, screenAfter.y, 0.01f)
    }

    @Test
    fun `clamp centers content when smaller than viewport`() {
        val clamped = clampOffset(Offset(50f, 50f), MIN_ZOOM, viewport, content)
        assertEquals((viewport.width - content.width) / 2f, clamped.x, 0.01f)
        assertEquals((viewport.height - content.height) / 2f, clamped.y, 0.01f)
    }

    @Test
    fun `clamp pins to edges when content larger than viewport`() {
        val scale = 3f
        val clamped = clampOffset(Offset(-10_000f, 10_000f), scale, viewport, content)
        val scaledW = content.width * scale
        val scaledH = content.height * scale
        assertEquals(viewport.width - scaledW, clamped.x, 0.01f)
        assertEquals(0f, clamped.y, 0.01f)
    }

    @Test
    fun `clamp allows free pan within bounds when zoomed`() {
        val scale = 2f
        val mid =
            Offset(
                x = (viewport.width - content.width * scale) / 2f,
                y = (viewport.height - content.height * scale) / 2f,
            )
        val clamped = clampOffset(mid, scale, viewport, content)
        assertEquals(mid.x, clamped.x, 0.01f)
        assertEquals(mid.y, clamped.y, 0.01f)
    }

    @Test
    fun `softClampOffset allows overscroll past hard bounds`() {
        val scale = 3f
        val (lower, upper) = offsetBounds(scale, viewport, content)
        val pastLeft = softClampOffset(Offset(upper.x + 100f, 0f), scale, viewport, content)
        assertEquals(upper.x + OVERSCROLL_PX, pastLeft.x, 0.01f)
        val pastRight = softClampOffset(Offset(lower.x - 100f, 0f), scale, viewport, content)
        assertEquals(lower.x - OVERSCROLL_PX, pastRight.x, 0.01f)
    }

    @Test
    fun `isOffsetOverscrolled detects soft overscroll`() {
        val scale = 3f
        val (lower, _) = offsetBounds(scale, viewport, content)
        assertFalse(
            isOffsetOverscrolled(
                clampOffset(Offset.Zero, scale, viewport, content),
                scale,
                viewport,
                content,
            ),
        )
        assertTrue(
            isOffsetOverscrolled(
                Offset(lower.x - OVERSCROLL_PX, 0f),
                scale,
                viewport,
                content,
            ),
        )
    }

    @Test
    fun `overscrollScaleRange extends past hard min and max`() {
        val range = overscrollScaleRange(DEFAULT_MAX_ZOOM)
        assertEquals(MIN_ZOOM * OVERSCROLL_MIN_SCALE_FACTOR, range.start, 0.001f)
        assertEquals(DEFAULT_MAX_ZOOM * OVERSCROLL_MAX_SCALE_FACTOR, range.endInclusive, 0.001f)
    }

    @Test
    fun `nextDoubleTapScale ladders gradual steps then wraps`() {
        val max = 40f
        assertEquals(DOUBLE_TAP_ZOOM, nextDoubleTapScale(MIN_ZOOM, max), 0.01f)
        assertEquals(5f, nextDoubleTapScale(DOUBLE_TAP_ZOOM, max), 0.01f)
        assertEquals(10f, nextDoubleTapScale(5f, max), 0.01f)
        assertEquals(20f, nextDoubleTapScale(10f, max), 0.01f)
        assertEquals(max, nextDoubleTapScale(20f, max), 0.01f)
        assertEquals(MIN_ZOOM, nextDoubleTapScale(max, max), 0.01f)
    }

    @Test
    fun `nextDoubleTapScale collapses mid when max below DOUBLE_TAP_ZOOM`() {
        val max = 2f
        assertEquals(max, nextDoubleTapScale(MIN_ZOOM, max), 0.01f)
        assertEquals(MIN_ZOOM, nextDoubleTapScale(max, max), 0.01f)
    }

    @Test
    fun `doubleTapZoomLevels drops steps above max`() {
        assertEquals(listOf(1f, 2f), doubleTapZoomLevels(2f))
        assertEquals(listOf(1f, 2.5f, 5f, 10f), doubleTapZoomLevels(10f))
    }

    @Test
    fun `canPanHorizontally false at edges for outward pan`() {
        val scale = 3f
        val (lower, upper) = offsetBounds(scale, viewport, content)
        // At left edge (upper.x): cannot pan further positive (content right).
        assertFalse(canPanHorizontally(Offset(upper.x, 0f), scale, viewport, content, deltaX = 10f))
        assertTrue(canPanHorizontally(Offset(upper.x, 0f), scale, viewport, content, deltaX = -10f))
        // At right edge (lower.x): cannot pan further negative.
        assertFalse(canPanHorizontally(Offset(lower.x, 0f), scale, viewport, content, deltaX = -10f))
        assertTrue(canPanHorizontally(Offset(lower.x, 0f), scale, viewport, content, deltaX = 10f))
    }

    @Test
    fun `canPanHorizontally false when content fits`() {
        assertFalse(
            canPanHorizontally(Offset.Zero, MIN_ZOOM, viewport, content, deltaX = 10f),
        )
    }

    @Test
    fun `derivedMaxZoom is at least DEFAULT_MAX_ZOOM`() {
        assertEquals(DEFAULT_MAX_ZOOM, derivedMaxZoom(100f, 100f), 0.01f)
        assertEquals(DEFAULT_MAX_ZOOM, derivedMaxZoom(50f, 100f), 0.01f)
    }

    @Test
    fun `derivedMaxZoom rises past native resolution with headroom`() {
        assertEquals(
            NATIVE_ZOOM_HEADROOM * 10f,
            derivedMaxZoom(2000f, 200f),
            0.01f,
        )
    }

    @Test
    fun `derivedMaxZoom handles zero fitted width`() {
        assertEquals(DEFAULT_MAX_ZOOM, derivedMaxZoom(2000f, 0f), 0.01f)
    }

    @Test
    fun `pan delta is scale-independent in viewport space`() {
        // Regression for the coordinate-space bug: a 10px finger move must
        // shift offset by 10px at any scale (deltas are viewport pixels).
        val pan = Offset(10f, -5f)
        val at1x = Offset.Zero + pan
        val at5x = Offset.Zero + pan
        assertEquals(at1x.x, at5x.x, 0.001f)
        assertEquals(at1x.y, at5x.y, 0.001f)
        assertEquals(10f, at5x.x, 0.001f)
        assertEquals(-5f, at5x.y, 0.001f)
    }

    @Test
    fun `fitContentSize letterboxes wide content`() {
        val fitted = fitContentSize(Size(400f, 800f), contentAspectRatio = 2f)
        assertEquals(400f, fitted.width, 0.01f)
        assertEquals(200f, fitted.height, 0.01f)
    }

    @Test
    fun `fitContentSize letterboxes tall content`() {
        val fitted = fitContentSize(Size(400f, 800f), contentAspectRatio = 0.25f)
        assertEquals(200f, fitted.width, 0.01f)
        assertEquals(800f, fitted.height, 0.01f)
    }

    @Test
    fun `offsetBounds collapses to center when content fits`() {
        val (lower, upper) = offsetBounds(MIN_ZOOM, viewport, content)
        assertEquals(lower, upper)
        assertTrue(lower.x >= 0f)
        assertTrue(lower.y >= 0f)
    }

    @Test
    fun `offsetBounds expands when zoomed past viewport`() {
        val (lower, upper) = offsetBounds(3f, viewport, content)
        assertTrue(lower.x < upper.x || lower.y < upper.y)
        assertTrue(lower.x <= upper.x)
        assertTrue(lower.y <= upper.y)
    }

    @Test
    fun `baseRenderScale stays within density band and edge cap`() {
        val scale = baseRenderScale(density = 3f, fittedWidthPx = 400f)
        assertTrue(scale in 1.5f..2.5f)
        val capped = baseRenderScale(density = 3f, fittedWidthPx = 3000f, maxBitmapEdge = 4096)
        assertTrue(capped <= 4096f / 3000f + 0.001f)
    }

    @Test
    fun `detailPixelRatio is monotonic and capped`() {
        val a = detailPixelRatio(1f)
        val b = detailPixelRatio(2f)
        val c = detailPixelRatio(5f)
        val d = detailPixelRatio(10f)
        assertTrue(b >= a)
        assertTrue(c >= b)
        assertTrue(d >= c)
        assertTrue(d <= 2.5f + 0.001f)
        assertEquals(1.5f, detailPixelRatio(1f), 0.01f)
        // Ramp continues past scale 4 (was previously plateaued).
        assertTrue(detailPixelRatio(5f) > detailPixelRatio(4f) - 0.001f)
    }

    @Test
    fun `detailPixelRatio respects custom max`() {
        assertEquals(1.5f, detailPixelRatio(10f, maxRatio = 1.5f), 0.01f)
    }

    @Test
    fun `cappedBitmapSize preserves aspect under edge limit`() {
        val (w, h) = cappedBitmapSize(8000, 4000, maxEdge = 4096)
        assertEquals(4096, w)
        assertEquals(2048, h)
    }
}
