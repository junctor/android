package com.advice.ui.components.zoom

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PdfTileMathTest {
    private val content = Size(1024f, 512f)
    private val viewport = Size(400f, 800f)

    @Test
    fun `selectTileLod picks smallest level at or above scale`() {
        assertEquals(1, selectTileLod(1f))
        assertEquals(2, selectTileLod(1.1f))
        assertEquals(2, selectTileLod(2f))
        assertEquals(8, selectTileLod(4.5f))
        assertEquals(8, selectTileLod(8f))
        assertEquals(64, selectTileLod(40f))
    }

    @Test
    fun `selectTileLod respects maxLod`() {
        assertEquals(8, selectTileLod(40f, maxLod = 8))
        assertEquals(4, selectTileLod(40f, maxLod = 4))
        assertEquals(1, selectTileLod(40f, maxLod = 1))
    }

    @Test
    fun `tile grid covers lod page`() {
        // LOD 1: 1024x512 -> 2x1 tiles of 512
        assertEquals(2, tileCountX(content, lod = 1))
        assertEquals(1, tileCountY(content, lod = 1))
        // LOD 2: 2048x1024 -> 4x2
        assertEquals(4, tileCountX(content, lod = 2))
        assertEquals(2, tileCountY(content, lod = 2))
    }

    @Test
    fun `tileContentRect maps lod pixels back to fitted content`() {
        val rect = tileContentRect(tx = 1, ty = 0, contentSize = content, lod = 1)
        assertEquals(512f, rect.left, 0.01f)
        assertEquals(0f, rect.top, 0.01f)
        assertEquals(512f, rect.width, 0.01f)
        assertEquals(512f, rect.height, 0.01f)
    }

    @Test
    fun `edge tileBitmapSize is clipped`() {
        val odd = Size(600f, 400f)
        val (w, h) = tileBitmapSize(tx = 1, ty = 0, contentSize = odd, lod = 1)
        assertEquals(88, w) // 600 - 512
        assertEquals(400, h)
    }

    @Test
    fun `visibleTileIndices includes prefetch ring`() {
        val region = ContentRegion(left = 100f, top = 50f, width = 200f, height = 100f)
        val withPrefetch = visibleTileIndices(region, content, lod = 1, prefetchRing = 1)
        val covering = coveringTileIndices(region, content, lod = 1)
        assertTrue(withPrefetch.containsAll(covering))
        assertTrue(withPrefetch.size >= covering.size)
    }

    @Test
    fun `visibleContentRegion matches zoom transform`() {
        val scale = 2f
        val offset = Offset(-100f, -50f)
        val region = visibleContentRegion(content, viewport, scale, offset)!!
        assertEquals(50f, region.left, 0.01f)
        assertEquals(25f, region.top, 0.01f)
        assertEquals(viewport.width / scale, region.width, 0.01f)
        assertEquals(viewport.height / scale, region.height, 0.01f)
    }

    @Test
    fun `buildNeededTileKeys orders low LOD before sharp`() {
        val keys =
            buildNeededTileKeys(
                pageIndex = 0,
                contentSize = content,
                viewport = viewport,
                scale = 5f,
                offset = Offset.Zero,
                maxLod = 16,
                prefetchRing = 0,
            )
        assertTrue(keys.isNotEmpty())
        assertEquals(1, keys.first().lod)
        assertTrue(keys.zipWithNext().all { (a, b) -> a.lod <= b.lod })
        assertTrue(keys.any { it.lod == 8 }) // selectTileLod(5) == 8
        assertFalse(keys.any { it.lod == 16 })
    }

    @Test
    fun `shouldDrawTileLod keeps all coarser levels`() {
        assertTrue(shouldDrawTileLod(tileLod = 8, currentLod = 8))
        assertTrue(shouldDrawTileLod(tileLod = 4, currentLod = 8))
        assertTrue(shouldDrawTileLod(tileLod = 1, currentLod = 8))
        assertFalse(shouldDrawTileLod(tileLod = 16, currentLod = 8))
    }

    @Test
    fun `pdfRegionToBitmapTransform maps region origin to bitmap origin`() {
        val region = ContentRegion(left = 100f, top = 50f, width = 200f, height = 100f)
        val (scale, translate) =
            pdfRegionToBitmapTransform(
                pageWidth = 1000f,
                pageHeight = 500f,
                contentSize = Size(500f, 250f),
                region = region,
                bitmapWidth = 400,
                bitmapHeight = 200,
            )
        // content/page = 0.5; bmp/region = 2 → scale = 1
        assertEquals(1f, scale.x, 0.001f)
        assertEquals(1f, scale.y, 0.001f)
        // content point (100,50) is region origin → bitmap (0,0)
        // pdf point for content (100,50): pdf = content / 0.5 = (200, 100)
        val bmpX = 200f * scale.x + translate.x
        val bmpY = 100f * scale.y + translate.y
        assertEquals(0f, bmpX, 0.01f)
        assertEquals(0f, bmpY, 0.01f)
    }
}
