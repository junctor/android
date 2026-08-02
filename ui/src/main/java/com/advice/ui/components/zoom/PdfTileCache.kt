package com.advice.ui.components.zoom

import android.graphics.Bitmap
import java.util.LinkedHashMap

/**
 * LRU cache of PDF tile bitmaps. Evicted bitmaps are recycled.
 */
internal class PdfTileCache(
    private val maxEntries: Int,
) {
    private val map =
        object : LinkedHashMap<TileKey, Bitmap>(maxEntries.coerceAtLeast(1), 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<TileKey, Bitmap>?): Boolean {
                if (size <= maxEntries) return false
                eldest?.value?.let { bmp ->
                    if (!bmp.isRecycled) {
                        runCatching { bmp.recycle() }
                    }
                }
                return true
            }
        }

    @Synchronized
    fun get(key: TileKey): Bitmap? {
        val bmp = map[key] ?: return null
        if (bmp.isRecycled) {
            map.remove(key)
            return null
        }
        return bmp
    }

    @Synchronized
    fun put(
        key: TileKey,
        bitmap: Bitmap,
    ) {
        map[key]?.let { previous ->
            if (previous !== bitmap && !previous.isRecycled) {
                runCatching { previous.recycle() }
            }
        }
        map[key] = bitmap
    }

    @Synchronized
    fun contains(key: TileKey): Boolean = get(key) != null

    /** Marks [keys] as recently used so they are not LRU-evicted next. */
    @Synchronized
    fun touch(keys: Iterable<TileKey>) {
        for (key in keys) {
            get(key)
        }
    }

    @Synchronized
    fun snapshot(): Map<TileKey, Bitmap> =
        map.entries
            .mapNotNull { (key, bmp) ->
                if (bmp.isRecycled) null else key to bmp
            }.toMap()

    @Synchronized
    fun keys(): Set<TileKey> = map.keys.toSet()

    @Synchronized
    fun clear() {
        for (bmp in map.values) {
            if (!bmp.isRecycled) {
                runCatching { bmp.recycle() }
            }
        }
        map.clear()
    }
}
