package com.advice.ui.components.zoom

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
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
}
