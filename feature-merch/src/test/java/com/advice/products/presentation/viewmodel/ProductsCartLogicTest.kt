package com.advice.products.presentation.viewmodel

import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductVariant
import com.advice.core.local.products.ProductVariantSelection
import com.advice.core.storage.MerchCartStore
import com.advice.products.utils.parseCompactOrderData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductsCartLogicTest {
    @Test
    fun resolveAndQr_bothInStock_includesBothVariants() {
        val products =
            listOf(
                product(id = 1, variantId = 100, stock = StockStatus.IN_STOCK, price = 1000),
                product(id = 2, variantId = 200, stock = StockStatus.IN_STOCK, price = 2000),
            )
        val selections =
            listOf(
                ProductVariantSelection(1, 100, 1),
                ProductVariantSelection(2, 200, 2),
            )

        val cart = resolveCartSelections(products, selections)
        val payload = cartQrPayload(cart, conference = 33, versionCode = 42)

        assertEquals(2, cart.size)
        assertEquals("1:33:A42:100:1;200:2", payload)
        assertEquals(5000L, cartSubtotalCents(cart))
    }

    @Test
    fun resolveAndQr_catalogMarksVariantOutOfStock_omitsFromQrAndSubtotal() {
        val selections =
            listOf(
                ProductVariantSelection(1, 100, 1),
                ProductVariantSelection(2, 200, 2),
            )
        // Catalog refresh: variant B becomes OOS while A stays in stock.
        val refreshed =
            listOf(
                product(id = 1, variantId = 100, stock = StockStatus.IN_STOCK, price = 1000),
                product(id = 2, variantId = 200, stock = StockStatus.OUT_OF_STOCK, price = 2000),
            )

        val cart = resolveCartSelections(refreshed, selections)
        val payload = cartQrPayload(cart, conference = 33, versionCode = 42)

        assertEquals(2, cart.size)
        assertEquals(StockStatus.OUT_OF_STOCK, cart[1].variant.stockStatus)
        assertEquals("1:33:A42:100:1", payload)
        assertEquals(listOf(100L to 1), parseCompactOrderData(payload!!).items.map { it.variantId to it.quantity })
        assertEquals(1000L, cartSubtotalCents(cart))
    }

    @Test
    fun resolveAndQr_allOutOfStock_payloadNullAndSubtotalZero() {
        val products =
            listOf(
                product(id = 1, variantId = 100, stock = StockStatus.OUT_OF_STOCK, price = 1000),
                product(id = 2, variantId = 200, stock = StockStatus.OUT_OF_STOCK, price = 2000),
            )
        val selections =
            listOf(
                ProductVariantSelection(1, 100, 1),
                ProductVariantSelection(2, 200, 1),
            )

        val cart = resolveCartSelections(products, selections)

        assertEquals(2, cart.size)
        assertNull(cartQrPayload(cart, conference = 33, versionCode = 1))
        assertEquals(0L, cartSubtotalCents(cart))
    }

    @Test
    fun pruneThenResolve_removedVariantDroppedFromCartAndQr() {
        val selections =
            listOf(
                ProductVariantSelection(1, 100, 1),
                ProductVariantSelection(2, 200, 1),
            )
        val products =
            listOf(
                product(id = 1, variantId = 100, stock = StockStatus.IN_STOCK, price = 1000),
                // product 2 / variant 200 gone from catalog
            )

        val pruned = MerchCartStore.pruneSelectionsToCatalog(selections, products)
        val cart = resolveCartSelections(products, pruned)
        val payload = cartQrPayload(cart, conference = 9, versionCode = 7)

        assertEquals(listOf(ProductVariantSelection(1, 100, 1)), pruned)
        assertEquals(1, cart.size)
        assertEquals("1:9:A7:100:1", payload)
    }

    @Test
    fun mandatoryAcknowledgementInfo_unseenNonBlank_returnsItem() {
        val info = mandatoryAcknowledgementInfo("Cash only.", hasSeen = false)

        assertEquals(1, info.size)
        assertEquals(MANDATORY_ACKNOWLEDGEMENT_KEY, info.single().key)
        assertEquals("Cash only.", info.single().text)
    }

    @Test
    fun mandatoryAcknowledgementInfo_blankOrSeen_returnsEmpty() {
        assertTrue(mandatoryAcknowledgementInfo(null, hasSeen = false).isEmpty())
        assertTrue(mandatoryAcknowledgementInfo("  ", hasSeen = false).isEmpty())
        assertTrue(mandatoryAcknowledgementInfo("Cash only.", hasSeen = true).isEmpty())
    }

    private fun product(
        id: Long,
        variantId: Long,
        stock: StockStatus,
        price: Long,
    ): Product {
        val variant =
            ProductVariant(
                id = variantId,
                label = "V$variantId",
                tags = emptyList(),
                price = price,
                stockStatus = stock,
            )
        return Product(
            id = id,
            code = "P$id",
            sortOrder = 0,
            label = "Product $id",
            baseCost = price,
            variants = listOf(variant),
            media = emptyList(),
            tags = emptyList(),
        )
    }
}
