package com.advice.core.storage

import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductVariant
import com.advice.core.local.products.ProductVariantSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MerchCartStoreTest {
    @Test
    fun sanitizeSelections_dropsNullAndNonPositiveQuantity() {
        val result =
            MerchCartStore.sanitizeSelections(
                listOf(
                    ProductVariantSelection(1, 10, 2),
                    ProductVariantSelection(2, 20, 0),
                    null,
                    ProductVariantSelection(3, 30, -1),
                    ProductVariantSelection(4, 40, 1),
                ),
            )

        assertEquals(
            listOf(
                ProductVariantSelection(1, 10, 2),
                ProductVariantSelection(4, 40, 1),
            ),
            result,
        )
    }

    @Test
    fun pruneSelectionsToCatalog_keepsOnlyExistingProductAndVariant() {
        val products =
            listOf(
                product(id = 1, variantIds = listOf(10L, 11L)),
                product(id = 2, variantIds = listOf(20L)),
            )
        val selections =
            listOf(
                ProductVariantSelection(1, 10, 1),
                ProductVariantSelection(1, 99, 1),
                ProductVariantSelection(2, 20, 2),
                ProductVariantSelection(3, 30, 1),
                ProductVariantSelection(1, null, 1),
            )

        val pruned = MerchCartStore.pruneSelectionsToCatalog(selections, products)

        assertEquals(
            listOf(
                ProductVariantSelection(1, 10, 1),
                ProductVariantSelection(2, 20, 2),
            ),
            pruned,
        )
    }

    @Test
    fun pruneSelectionsToCatalog_emptyCatalogClearsAll() {
        val selections = listOf(ProductVariantSelection(1, 10, 1))
        assertTrue(MerchCartStore.pruneSelectionsToCatalog(selections, emptyList()).isEmpty())
    }

    @Test
    fun pruneSelectionsToCatalog_keepsOutOfStockVariants() {
        val products =
            listOf(
                product(id = 1, variantIds = listOf(10L), stockStatus = StockStatus.OUT_OF_STOCK),
            )
        val selections = listOf(ProductVariantSelection(1, 10, 2))

        val pruned = MerchCartStore.pruneSelectionsToCatalog(selections, products)

        assertEquals(selections, pruned)
    }

    private fun product(
        id: Long,
        variantIds: List<Long>,
        stockStatus: StockStatus = StockStatus.IN_STOCK,
    ): Product =
        Product(
            id = id,
            code = "P$id",
            sortOrder = 0,
            label = "Product $id",
            baseCost = 1000,
            variants =
                variantIds.map { variantId ->
                    ProductVariant(
                        id = variantId,
                        label = "V$variantId",
                        tags = emptyList(),
                        price = 1000,
                        stockStatus = stockStatus,
                    )
                },
            media = emptyList(),
            tags = emptyList(),
        )
}
