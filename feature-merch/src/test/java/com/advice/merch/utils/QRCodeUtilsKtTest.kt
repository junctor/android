package com.advice.merch.utils

import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductSelection
import com.advice.core.local.products.ProductVariant
import com.advice.core.local.products.ProductVariantSelection
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
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

    @Test
    fun `ProductSelection empty list returns null`() {
        assertNull(emptyList<ProductSelection>().toStringData(conference = 123, versionCode = 1))
    }

    @Test
    fun `ProductSelection null conference returns null`() {
        val selection = productSelection(StockStatus.IN_STOCK)
        assertNull(listOf(selection).toStringData(conference = null, versionCode = 1))
    }

    @Test
    fun `ProductSelection out of stock lines are omitted from QR`() {
        val inStock = productSelection(StockStatus.IN_STOCK, productId = 1, variantId = 456)
        val outOfStock = productSelection(StockStatus.OUT_OF_STOCK, productId = 2, variantId = 789)
        val data =
            listOf(inStock, outOfStock).toStringData(conference = 123, versionCode = 42)

        assertEquals("1:123:A42:456:1", data)
    }

    @Test
    fun `ProductSelection all out of stock returns null`() {
        val outOfStock = productSelection(StockStatus.OUT_OF_STOCK)
        assertNull(listOf(outOfStock).toStringData(conference = 123, versionCode = 1))
    }

    @Test
    fun `ProductSelection multi-line omits only out of stock variants`() {
        val a = productSelection(StockStatus.IN_STOCK, productId = 1, variantId = 100)
        val b = productSelection(StockStatus.OUT_OF_STOCK, productId = 2, variantId = 200)
        val c = productSelection(StockStatus.IN_STOCK, productId = 3, variantId = 300)
        val data = listOf(a, b, c).toStringData(conference = 55, versionCode = 9)

        assertEquals("1:55:A9:100:1;300:1", data)
    }

    private fun productSelection(
        stockStatus: StockStatus,
        productId: Long = 1,
        variantId: Long = 100,
    ): ProductSelection {
        val variant =
            ProductVariant(
                id = variantId,
                label = "One",
                tags = emptyList(),
                price = 1000,
                stockStatus = stockStatus,
            )
        val product =
            Product(
                id = productId,
                code = "P$productId",
                sortOrder = 0,
                label = "Product $productId",
                baseCost = 1000,
                variants = listOf(variant),
                media = emptyList(),
                tags = emptyList(),
            )
        return ProductSelection(product = product, variant = variant, quantity = 1)
    }
}
