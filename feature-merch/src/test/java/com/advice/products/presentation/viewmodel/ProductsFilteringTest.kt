package com.advice.products.presentation.viewmodel

import com.advice.core.local.StockStatus
import com.advice.core.local.Tag
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class ProductsFilteringTest {
    private val sizeM = Tag(100, "M", "", null, 0)
    private val sizeL = Tag(101, "L", "", null, 1)
    private val apparel = Tag(1, "Apparel", "", null, 1)

    @Test
    fun emptyFilter_returnsProductsUnchanged() {
        val products = listOf(product(requiresVariants = false))

        assertSame(products, getFilteredProducts(products, emptyList()))
    }

    @Test
    fun variantFilter_setsInStockOverrideWhenMatchingVariantInStock() {
        val product =
            product(
                requiresVariants = true,
                variants =
                    listOf(
                        ProductVariant(1, "M", listOf(sizeM.id), 1000, StockStatus.IN_STOCK),
                        ProductVariant(2, "L", listOf(sizeL.id), 1000, StockStatus.OUT_OF_STOCK),
                    ),
            )

        val filtered = getFilteredProducts(listOf(product), listOf(sizeM))

        assertEquals(StockStatus.IN_STOCK, filtered.single().stockStatusOverride)
    }

    @Test
    fun variantFilter_setsOutOfStockOverrideWhenNoMatchingInStockVariant() {
        val product =
            product(
                requiresVariants = true,
                variants =
                    listOf(
                        ProductVariant(1, "M", listOf(sizeM.id), 1000, StockStatus.OUT_OF_STOCK),
                        ProductVariant(2, "L", listOf(sizeL.id), 1000, StockStatus.IN_STOCK),
                    ),
            )

        val filtered = getFilteredProducts(listOf(product), listOf(sizeM))

        assertEquals(StockStatus.OUT_OF_STOCK, filtered.single().stockStatusOverride)
    }

    @Test
    fun groupProducts_usesOutOfStockGroup() {
        val outOfStock =
            product(
                requiresVariants = true,
                variants = listOf(ProductVariant(1, "One", emptyList(), 1000, StockStatus.OUT_OF_STOCK)),
                tags = listOf(apparel),
            )

        val groups = groupProducts(listOf(outOfStock))

        assertEquals("Out of Stock", groups.keys.single().label)
    }

    @Test
    fun groupProducts_usesAvailableInOtherSizesWhenOverrideOutAndBaseIn() {
        val product =
            product(
                requiresVariants = true,
                variants =
                    listOf(
                        ProductVariant(1, "M", listOf(sizeM.id), 1000, StockStatus.IN_STOCK),
                        ProductVariant(2, "L", listOf(sizeL.id), 1000, StockStatus.OUT_OF_STOCK),
                    ),
                tags = listOf(apparel),
                stockStatusOverride = StockStatus.OUT_OF_STOCK,
            )

        val groups = groupProducts(listOf(product))

        assertEquals("Available in other sizes", groups.keys.single().label)
    }

    @Test
    fun groupProducts_usesFirstTagThenOtherAndSortsBySortOrder() {
        val tagged =
            product(
                requiresVariants = false,
                tags = listOf(apparel),
                variants = listOf(ProductVariant(1, "One", emptyList(), 1000, StockStatus.IN_STOCK)),
            )
        val untagged =
            product(
                id = 2,
                requiresVariants = false,
                tags = emptyList(),
                variants = listOf(ProductVariant(2, "One", emptyList(), 1000, StockStatus.IN_STOCK)),
            )

        val groups = groupProducts(listOf(tagged, untagged))

        assertEquals(listOf("Apparel", "Other"), groups.keys.map { it.label })
    }

    private fun product(
        id: Long = 1,
        requiresVariants: Boolean,
        variants: List<ProductVariant> =
            if (requiresVariants) {
                listOf(
                    ProductVariant(1, "M", listOf(sizeM.id), 1000, StockStatus.IN_STOCK),
                    ProductVariant(2, "L", listOf(sizeL.id), 1000, StockStatus.IN_STOCK),
                )
            } else {
                listOf(ProductVariant(1, "One", emptyList(), 1000, StockStatus.IN_STOCK))
            },
        tags: List<Tag> = emptyList(),
        stockStatusOverride: StockStatus? = null,
    ) = Product(
        id = id,
        code = "P$id",
        sortOrder = id.toInt(),
        label = "Product $id",
        baseCost = 1000,
        variants = variants,
        media = emptyList(),
        tags = tags,
        stockStatusOverride = stockStatusOverride,
    )
}
