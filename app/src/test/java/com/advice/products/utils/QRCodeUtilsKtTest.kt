package com.advice.products.utils

import com.advice.core.local.products.ProductVariantSelection
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Test

class QRCodeUtilsKtTest {
    // 1:123:A42:456:2;789:3
    @Test
    fun `test generate qr code data string`() {
        val products =
            listOf(
                ProductVariantSelection(id = 1, variant = 456, quantity = 2),
                ProductVariantSelection(id = 2, variant = 789, quantity = 3),
            )

        val data = products.toStringData(conference = 123, versionCode = 42)

        assertEquals("String data does not match", "1:123:A42:456:2;789:3", data)
    }

    @Test
    fun `parseCompactOrderData round-trips encode`() {
        val products =
            listOf(
                ProductVariantSelection(id = 1, variant = 456, quantity = 2),
                ProductVariantSelection(id = 2, variant = 789, quantity = 3),
            )
        val encoded = products.toStringData(conference = 123, versionCode = 42)
        val parsed = parseCompactOrderData(encoded)

        assertEquals(1, parsed.version)
        assertEquals(123L, parsed.conferenceId)
        assertEquals("A42", parsed.platform)
        assertEquals(5, parsed.totalQuantity)
        assertEquals(
            listOf(
                CompactOrderItem(456, 2),
                CompactOrderItem(789, 3),
            ),
            parsed.items,
        )
    }

    @Test
    fun `parseCompactOrderData rejects empty cart payload`() {
        assertThrows(IllegalArgumentException::class.java) {
            parseCompactOrderData("1:123:A42:")
        }
    }

    @Test
    fun `parseCompactOrderData rejects legacy json`() {
        assertThrows(IllegalArgumentException::class.java) {
            parseCompactOrderData("""{"products":[{"id":1,"quantity":1}]}""")
        }
    }

    @Test
    fun `single line compact order is valid`() {
        val encoded =
            listOf(ProductVariantSelection(id = 9, variant = 1001, quantity = 1))
                .toStringData(conference = 33, versionCode = 428)
        val parsed = parseCompactOrderData(encoded)
        assertEquals(1, parsed.version)
        assertTrue(parsed.platform.matches(Regex("A\\d+")))
        assertEquals(listOf(CompactOrderItem(1001, 1)), parsed.items)
    }
}
