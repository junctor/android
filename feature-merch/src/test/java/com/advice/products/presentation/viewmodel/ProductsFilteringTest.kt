package com.advice.products.presentation.viewmodel

import com.advice.core.local.StockStatus
import com.advice.core.local.Tag
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
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
    fun variantFilter_keepsProductWhenSelectedSizeInStock() {
        val product =
            product(
                requiresVariants = true,
                variants =
                    listOf(
                        ProductVariant(1, "M", listOf(sizeM.id), 1000, StockStatus.IN_STOCK, code = "M"),
                        ProductVariant(2, "L", listOf(sizeL.id), 1000, StockStatus.OUT_OF_STOCK, code = "L"),
                    ),
            )

        val filtered = getFilteredProducts(listOf(product), listOf(sizeM))

        assertEquals(StockStatus.IN_STOCK, filtered.single().stockStatusOverride)
    }

    @Test
    fun variantFilter_keepsProductWhenSelectedSizeIsLowStock() {
        val product =
            product(
                requiresVariants = true,
                variants =
                    listOf(
                        ProductVariant(1, "S", emptyList(), 1000, StockStatus.LOW_STOCK, code = "S"),
                        ProductVariant(2, "L", emptyList(), 1000, StockStatus.OUT_OF_STOCK, code = "L"),
                    ),
            )

        val filtered = getFilteredProducts(listOf(product), listOf(Tag(1, "S", "", null, 0)))

        assertEquals(1, filtered.size)
    }

    @Test
    fun variantFilter_excludesProductWhenSelectedSizeOutOfStock() {
        // DC32 Blue Tank: L is OUT, only S is LOW — filtering to L must hide it.
        val product =
            product(
                requiresVariants = true,
                variants =
                    listOf(
                        ProductVariant(1, "Extra-Small", emptyList(), 4000, StockStatus.OUT_OF_STOCK, code = "XS"),
                        ProductVariant(2, "Small", emptyList(), 4000, StockStatus.LOW_STOCK, code = "S"),
                        ProductVariant(3, "Medium", emptyList(), 4000, StockStatus.OUT_OF_STOCK, code = "M"),
                        ProductVariant(4, "Large", emptyList(), 4000, StockStatus.OUT_OF_STOCK, code = "L"),
                        ProductVariant(5, "1-XL", emptyList(), 4000, StockStatus.OUT_OF_STOCK, code = "1X"),
                    ),
            )

        val filtered = getFilteredProducts(listOf(product), listOf(sizeL))

        assertTrue(filtered.isEmpty())
    }

    @Test
    fun variantFilter_matchesByRecognizedSizeCodeWhenTagsEmpty() {
        val product =
            product(
                requiresVariants = true,
                variants =
                    listOf(
                        ProductVariant(1, "Medium", emptyList(), 1000, StockStatus.OUT_OF_STOCK, code = "M"),
                        ProductVariant(2, "Large", emptyList(), 1000, StockStatus.IN_STOCK, code = "L"),
                    ),
            )
        val filter = Tag(SIZE_FILTER_TAG_ID_BASE, "L", "", null, 0)

        val filtered = getFilteredProducts(listOf(product), listOf(filter))

        assertEquals(StockStatus.IN_STOCK, filtered.single().stockStatusOverride)
    }

    @Test
    fun variantFilter_passesThroughProductsWithNoRecognizedSizeCodes() {
        val product =
            product(
                requiresVariants = true,
                variants =
                    listOf(
                        ProductVariant(1, "L", emptyList(), 1000, StockStatus.IN_STOCK, code = "LARGE"),
                        ProductVariant(2, "M", emptyList(), 1000, StockStatus.IN_STOCK, code = "MEDIUM"),
                    ),
            )
        val filter = Tag(SIZE_FILTER_TAG_ID_BASE, "L", "", null, 0)

        val filtered = getFilteredProducts(listOf(product), listOf(filter))

        assertEquals(1, filtered.size)
    }

    @Test
    fun sizeFilterTagType_buildsUniqueSortedRecognizedSizeCodes() {
        val product =
            product(
                requiresVariants = true,
                variants =
                    listOf(
                        ProductVariant(1, "Large", emptyList(), 1000, StockStatus.IN_STOCK, code = "L"),
                        ProductVariant(2, "Small", emptyList(), 1000, StockStatus.IN_STOCK, code = "S"),
                        ProductVariant(3, "1X", emptyList(), 1000, StockStatus.IN_STOCK, code = "1X"),
                        ProductVariant(4, "Medium", emptyList(), 1000, StockStatus.OUT_OF_STOCK, code = "M"),
                        ProductVariant(5, "One Size", emptyList(), 1000, StockStatus.IN_STOCK, code = "OS"),
                        ProductVariant(6, "Widget", emptyList(), 1000, StockStatus.IN_STOCK, code = ""),
                    ),
            )

        val tagType = sizeFilterTagType(listOf(product), selectedLabels = setOf("M"))

        assertEquals("Size", tagType!!.label)
        assertEquals(listOf("S", "M", "L", "1X"), tagType.tags.map { it.label })
        assertTrue(tagType.tags.single { it.label == "M" }.isSelected)
        assertTrue(tagType.tags.filterNot { it.label == "M" }.none { it.isSelected })
    }

    @Test
    fun sizeFilterTagType_returnsNullWhenNoRecognizedCodes() {
        assertNull(sizeFilterTagType(emptyList()))
        assertNull(
            sizeFilterTagType(
                listOf(
                    product(
                        requiresVariants = true,
                        variants =
                            listOf(
                                ProductVariant(1, "One Size", emptyList(), 1000, StockStatus.IN_STOCK, code = "OS"),
                            ),
                    ),
                ),
            ),
        )
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
                        ProductVariant(1, "M", listOf(sizeM.id), 1000, StockStatus.IN_STOCK, code = "M"),
                        ProductVariant(2, "L", listOf(sizeL.id), 1000, StockStatus.OUT_OF_STOCK, code = "L"),
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
                    ProductVariant(1, "M", listOf(sizeM.id), 1000, StockStatus.IN_STOCK, code = "M"),
                    ProductVariant(2, "L", listOf(sizeL.id), 1000, StockStatus.IN_STOCK, code = "L"),
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
