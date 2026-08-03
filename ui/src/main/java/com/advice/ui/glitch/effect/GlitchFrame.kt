package com.advice.ui.glitch.effect

/**
 * Parameters for a single glitch frame / burst.
 *
 * Pixel values are relative to the source bitmap size.
 */
data class GlitchFrame(
    val chromaOffsetPx: Int = 0,
    val slices: List<Slice> = emptyList(),
    val blocks: List<CorruptBlock> = emptyList(),
    /** 0–255 opacity for CRT-style horizontal scanlines. */
    val scanlineAlpha: Int = 0,
) {
    val isIdle: Boolean
        get() =
            chromaOffsetPx == 0 &&
                slices.isEmpty() &&
                blocks.isEmpty() &&
                scanlineAlpha == 0

    data class Slice(
        val y: Int,
        val height: Int,
        val dx: Int,
    )

    data class CorruptBlock(
        val x: Int,
        val y: Int,
        val width: Int,
        val height: Int,
        val color: Int,
    )

    companion object {
        val IDLE = GlitchFrame()
    }
}
