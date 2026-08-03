package com.advice.ui.glitch.effect

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GlitchRngTest {
    @Test
    fun maxChromaPx_scalesWithWidthAndIntensity() {
        assertEquals(24, GlitchRng.maxChromaPx(432, 1f))
        assertEquals(12, GlitchRng.maxChromaPx(432, 0.5f))
        assertEquals(0, GlitchRng.maxChromaPx(432, 0f))
    }

    @Test
    fun maxSliceDxPx_clampedToEightPercentWidth() {
        assertEquals(35, GlitchRng.maxSliceDxPx(432, 1f)) // round(432 * 0.08)
        assertEquals(0, GlitchRng.maxSliceDxPx(432, 0f))
    }

    @Test
    fun maxBlockSizePx_clampedToFourPercentMinSide() {
        assertEquals(17, GlitchRng.maxBlockSizePx(432, 432, 1f)) // round(432 * 0.04)
        assertEquals(1, GlitchRng.maxBlockSizePx(10, 10, 1f))
    }

    @Test
    fun nextBurstFrame_zeroIntensity_isIdle() {
        val frame = GlitchRng(Random(0)).nextBurstFrame(432, 432, intensity = 0f)
        assertTrue(frame.isIdle)
    }

    @Test
    fun nextBurstFrame_valuesStayWithinClamps() {
        val rng = GlitchRng(Random(42))
        val width = 432
        val height = 432
        val intensity = 1f
        val maxChroma = GlitchRng.maxChromaPx(width, intensity)
        val maxDx = GlitchRng.maxSliceDxPx(width, intensity)
        val maxBlock = GlitchRng.maxBlockSizePx(width, height, intensity)

        repeat(50) {
            val frame = rng.nextBurstFrame(width, height, intensity, enableScanlines = true)
            assertTrue(frame.chromaOffsetPx in 0..maxChroma)
            assertTrue(frame.slices.size <= 5)
            for (slice in frame.slices) {
                assertTrue(slice.y >= 0)
                assertTrue(slice.height >= 1)
                assertTrue(slice.y + slice.height <= height)
                assertTrue(slice.dx in -maxDx..maxDx)
                assertTrue(slice.dx != 0)
            }
            for (block in frame.blocks) {
                assertTrue(block.width in 1..maxBlock)
                assertTrue(block.height in 1..maxBlock)
                assertTrue(block.x >= 0 && block.x + block.width <= width)
                assertTrue(block.y >= 0 && block.y + block.height <= height)
            }
            assertTrue(frame.scanlineAlpha in 20..40)
            // At least one effect must fire each burst.
            assertTrue(
                frame.chromaOffsetPx > 0 || frame.slices.isNotEmpty() || frame.blocks.isNotEmpty(),
            )
        }
    }

    @Test
    fun nextBurstFrame_effectsOccurIndependentlyAcrossBursts() {
        val rng = GlitchRng(Random(99))
        var chromaOnly = 0
        var sliceOnly = 0
        var corruptOnly = 0
        var multi = 0

        repeat(200) {
            val frame = rng.nextBurstFrame(432, 432, intensity = 1f)
            val hasChroma = frame.chromaOffsetPx > 0
            val hasSlice = frame.slices.isNotEmpty()
            val hasCorrupt = frame.blocks.isNotEmpty()
            val count = listOf(hasChroma, hasSlice, hasCorrupt).count { it }
            when {
                count > 1 -> multi++
                hasChroma -> chromaOnly++
                hasSlice -> sliceOnly++
                hasCorrupt -> corruptOnly++
            }
        }

        assertTrue("expected some chroma-only bursts", chromaOnly > 0)
        assertTrue("expected some slice-only bursts", sliceOnly > 0)
        assertTrue("expected some corruption-only bursts", corruptOnly > 0)
        assertTrue("expected some multi-effect clusters", multi > 0)
    }

    @Test
    fun nextBurstFrame_scanlinesDisabled_alphaZero() {
        val frame =
            GlitchRng(Random(1)).nextBurstFrame(
                width = 200,
                height = 200,
                intensity = 1f,
                enableScanlines = false,
            )
        assertEquals(0, frame.scanlineAlpha)
    }

    @Test
    fun delays_areWithinConfiguredRanges() {
        val rng = GlitchRng(Random(7))
        repeat(20) {
            val idle = rng.nextIdleDelayMs()
            val burst = rng.nextBurstDurationMs()
            assertTrue(idle in 400L..2_000L)
            assertTrue(burst in 150L..350L)
        }
    }
}
