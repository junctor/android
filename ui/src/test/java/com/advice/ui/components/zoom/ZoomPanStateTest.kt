package com.advice.ui.components.zoom

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ZoomPanStateTest {
    private val viewport = Size(400f, 800f)
    private val content = Size(400f, 600f)

    private fun stateAt(
        scale: Float,
        offset: Offset = Offset.Zero,
        maxZoom: Float = DEFAULT_MAX_ZOOM,
    ): ZoomPanState =
        ZoomPanState(initialScale = scale, initialOffset = offset).also {
            it.animationsEnabled = false
            it.updateLayout(viewport, content, maxZoom)
            it.snapToLayout()
        }

    @Test
    fun `settleAfterGesture clamps overscrolled scale and offset`() =
        runBlocking {
            val state = stateAt(MIN_ZOOM)
            state.applyTransform(
                zoomChange = OVERSCROLL_MAX_SCALE_FACTOR * DEFAULT_MAX_ZOOM,
                panChange = Offset(-10_000f, 10_000f),
                centroid = Offset(200f, 400f),
                overscroll = true,
            )
            assertTrue(state.isOverscrolled() || state.scale > DEFAULT_MAX_ZOOM - 0.01f)

            state.settleAfterGesture()

            assertEquals(DEFAULT_MAX_ZOOM, state.scale, 0.01f)
            val clamped = clampOffset(state.offset, state.scale, viewport, content)
            assertEquals(clamped.x, state.offset.x, 0.5f)
            assertEquals(clamped.y, state.offset.y, 0.5f)
            assertFalse(state.isOverscrolled())
        }

    @Test
    fun `animateDoubleTap cycles gradual ladder then fit`() =
        runBlocking {
            val maxZoom = 8f
            val state = stateAt(MIN_ZOOM, maxZoom = maxZoom)
            val tap = Offset(200f, 400f)

            state.animateDoubleTap(tap)
            assertEquals(DOUBLE_TAP_ZOOM, state.scale, 0.01f)

            state.animateDoubleTap(tap)
            assertEquals(5f, state.scale, 0.01f)

            state.animateDoubleTap(tap)
            assertEquals(maxZoom, state.scale, 0.01f)

            state.animateDoubleTap(tap)
            assertEquals(MIN_ZOOM, state.scale, 0.01f)
        }

    @Test
    fun `canPanHorizontally delegates to edge math`() {
        val scale = 3f
        val (lower, upper) = offsetBounds(scale, viewport, content)
        val atLeft = stateAt(scale, Offset(upper.x, lower.y))
        assertFalse(atLeft.canPanHorizontally(10f))
        assertTrue(atLeft.canPanHorizontally(-10f))

        val atRight = stateAt(scale, Offset(lower.x, lower.y))
        assertFalse(atRight.canPanHorizontally(-10f))
        assertTrue(atRight.canPanHorizontally(10f))
    }

    @Test
    fun `applyTransform without overscroll stays in hard bounds`() {
        val state = stateAt(MIN_ZOOM)
        state.applyTransform(
            zoomChange = 100f,
            panChange = Offset(-9999f, 9999f),
            centroid = Offset(100f, 100f),
            overscroll = false,
        )
        assertEquals(DEFAULT_MAX_ZOOM, state.scale, 0.01f)
        assertFalse(state.isOverscrolled())
    }

    @Test
    fun `animateZoomByStep multiplies scale by button step`() =
        runBlocking {
            val state = stateAt(MIN_ZOOM)
            state.animateZoomByStep(ZOOM_BUTTON_STEP)
            assertEquals(ZOOM_BUTTON_STEP, state.scale, 0.01f)
            state.animateZoomByStep(1f / ZOOM_BUTTON_STEP)
            assertEquals(MIN_ZOOM, state.scale, 0.01f)
        }

    @Test
    fun `pinch past max zoom does not drift offset via zoomAround`() {
        val state = stateAt(DEFAULT_MAX_ZOOM, offset = Offset(-50f, -80f))
        val before = state.offset
        state.applyTransform(
            zoomChange = 1.2f,
            panChange = Offset.Zero,
            centroid = Offset(350f, 700f),
            overscroll = true,
        )
        assertEquals(DEFAULT_MAX_ZOOM, state.scale, 0.01f)
        assertEquals(before.x, state.offset.x, 0.01f)
        assertEquals(before.y, state.offset.y, 0.01f)
    }

    @Test
    fun `animateZoomByStep chains from current scale after stop`() =
        runBlocking {
            val state = stateAt(MIN_ZOOM)
            state.animationsEnabled = false
            state.animateZoomByStep(ZOOM_BUTTON_STEP)
            assertEquals(ZOOM_BUTTON_STEP, state.scale, 0.01f)
            state.animateZoomByStep(ZOOM_BUTTON_STEP)
            assertEquals(ZOOM_BUTTON_STEP * ZOOM_BUTTON_STEP, state.scale, 0.01f)
        }

    @Test
    fun `reduced motion snaps settle immediately`() =
        runBlocking {
            val state = stateAt(MIN_ZOOM)
            state.animationsEnabled = false
            state.applyTransform(
                zoomChange = 2f,
                panChange = Offset.Zero,
                centroid = Offset(200f, 400f),
                overscroll = true,
            )
            // Force soft overscroll on Y if possible
            state.applyTransform(
                zoomChange = 1f,
                panChange = Offset(0f, -5000f),
                centroid = Offset(200f, 400f),
                overscroll = true,
            )
            state.settleAfterGesture()
            assertFalse(state.isOverscrolled())
            assertTrue(state.scale in MIN_ZOOM..DEFAULT_MAX_ZOOM)
        }
}
