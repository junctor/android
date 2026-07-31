package com.advice.glitch.effect

import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Generates clamped, seedable [GlitchFrame] values that approximate the
 * `logo_glitch.png` / DEF CON 28 Safe Mode look.
 *
 * Chroma, slice tears, and corruption are chosen independently each burst
 * (with clustering so 1–2 effects are most common, all three less often).
 */
class GlitchRng(
    private val random: Random = Random.Default,
) {
    fun nextBurstFrame(
        width: Int,
        height: Int,
        intensity: Float = 1f,
        enableScanlines: Boolean = false,
    ): GlitchFrame {
        require(width > 0 && height > 0) { "width and height must be positive" }
        val i = intensity.coerceIn(0f, 1f)
        if (i <= 0f) return GlitchFrame.IDLE

        val active = pickActiveEffects()
        val chroma =
            if (Effect.CHROMA in active) {
                nextChroma(width, i)
            } else {
                0
            }
        val slices =
            if (Effect.SLICE in active) {
                nextSlices(width, height, i)
            } else {
                emptyList()
            }
        val blocks =
            if (Effect.CORRUPTION in active) {
                nextBlocks(width, height, i)
            } else {
                emptyList()
            }

        val scanlineAlpha =
            if (enableScanlines) {
                random.nextInt(MIN_SCANLINE_ALPHA, MAX_SCANLINE_ALPHA + 1)
            } else {
                0
            }

        return GlitchFrame(
            chromaOffsetPx = chroma,
            slices = slices,
            blocks = blocks,
            scanlineAlpha = scanlineAlpha,
        )
    }

    fun nextIdleDelayMs(): Long = random.nextLong(MIN_IDLE_MS, MAX_IDLE_MS + 1)

    fun nextBurstDurationMs(): Long = random.nextLong(MIN_BURST_MS, MAX_BURST_MS + 1)

    private fun pickActiveEffects(): Set<Effect> {
        // Cluster size: mostly one effect, often two, rarely all three.
        val roll = random.nextFloat()
        val count =
            when {
                roll < 0.55f -> 1
                roll < 0.90f -> 2
                else -> 3
            }
        return Effect.entries
            .shuffled(random)
            .take(count)
            .toSet()
    }

    private fun nextChroma(
        width: Int,
        intensity: Float,
    ): Int {
        val maxChroma = maxChromaPx(width, intensity)
        if (maxChroma <= 0) return 0
        return random.nextInt(max(1, maxChroma / 3), maxChroma + 1)
    }

    private fun nextSlices(
        width: Int,
        height: Int,
        intensity: Float,
    ): List<GlitchFrame.Slice> {
        val maxSliceDx = maxSliceDxPx(width, intensity)
        if (maxSliceDx <= 0) return emptyList()

        val sliceCount = random.nextInt(MIN_SLICES, MAX_SLICES + 1)
        return List(sliceCount) {
            val minH = max(1, (height * MIN_SLICE_HEIGHT_FRAC * intensity).roundToInt())
            val maxHExclusive =
                max(minH + 1, (height * MAX_SLICE_HEIGHT_FRAC * intensity).roundToInt() + 1)
                    .coerceAtMost(height + 1)
            val sliceHeight = random.nextInt(minH, maxHExclusive).coerceAtMost(height)
            val y = random.nextInt(0, max(1, height - sliceHeight + 1))
            val magnitude = random.nextInt(1, maxSliceDx + 1)
            val dx = if (random.nextBoolean()) magnitude else -magnitude
            GlitchFrame.Slice(y = y, height = sliceHeight, dx = dx)
        }
    }

    private fun nextBlocks(
        width: Int,
        height: Int,
        intensity: Float,
    ): List<GlitchFrame.CorruptBlock> {
        val maxBlock = maxBlockSizePx(width, height, intensity)
        val blockCount = random.nextInt(MIN_BLOCKS, MAX_BLOCKS + 1)
        val sizeExclusive = max(2, maxBlock + 1)
        return List(blockCount) {
            val bw = random.nextInt(1, sizeExclusive)
            val bh = random.nextInt(1, sizeExclusive)
            GlitchFrame.CorruptBlock(
                x = random.nextInt(0, max(1, width - bw + 1)),
                y = random.nextInt(0, max(1, height - bh + 1)),
                width = bw,
                height = bh,
                color = BLOCK_COLORS[random.nextInt(BLOCK_COLORS.size)],
            )
        }
    }

    private enum class Effect {
        CHROMA,
        SLICE,
        CORRUPTION,
    }

    companion object {
        private const val REF_WIDTH = 432f

        /** Max chroma ~24px at 432px reference width. */
        private const val MAX_CHROMA_FRAC = 24f / REF_WIDTH

        /** Max slice tear ~8% of width. */
        private const val MAX_SLICE_DX_FRAC = 0.08f

        private const val MIN_SLICE_HEIGHT_FRAC = 0.02f
        private const val MAX_SLICE_HEIGHT_FRAC = 0.12f

        private const val MIN_SLICES = 1
        private const val MAX_SLICES = 5
        private const val MIN_BLOCKS = 1
        private const val MAX_BLOCKS = 8
        private const val MAX_BLOCK_FRAC = 0.04f

        private const val MIN_SCANLINE_ALPHA = 20
        private const val MAX_SCANLINE_ALPHA = 40

        private const val MIN_IDLE_MS = 400L
        private const val MAX_IDLE_MS = 2_000L
        private const val MIN_BURST_MS = 150L
        private const val MAX_BURST_MS = 350L

        private val BLOCK_COLORS =
            intArrayOf(
                0xFFFF0000.toInt(),
                0xFF00FFFF.toInt(),
                0xFFFFFFFF.toInt(),
            )

        fun maxChromaPx(
            width: Int,
            intensity: Float = 1f,
        ): Int {
            val i = intensity.coerceIn(0f, 1f)
            return max(0, (width * MAX_CHROMA_FRAC * i).roundToInt())
        }

        fun maxSliceDxPx(
            width: Int,
            intensity: Float = 1f,
        ): Int {
            val i = intensity.coerceIn(0f, 1f)
            return max(0, (width * MAX_SLICE_DX_FRAC * i).roundToInt())
        }

        fun maxBlockSizePx(
            width: Int,
            height: Int,
            intensity: Float = 1f,
        ): Int {
            val i = intensity.coerceIn(0f, 1f)
            val basis = minOf(width, height)
            return max(1, (basis * MAX_BLOCK_FRAC * i).roundToInt())
        }
    }
}
