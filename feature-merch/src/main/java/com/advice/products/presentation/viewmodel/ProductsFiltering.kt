package com.advice.products.presentation.viewmodel

import com.advice.core.local.StockStatus
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductVariant

internal const val SIZE_FILTER_TYPE_ID = -100L
internal const val SIZE_FILTER_TAG_ID_BASE = -1000L

internal fun groupProducts(products: List<Product>): Map<Tag, List<Product>> {
    val outOfStockGroup = Tag(-1, "Out of Stock", "", "", 1000)
    val availableInOtherSizes = Tag(-2, "Available in other sizes", "", "", 100)
    val defaultGroup = Tag(-3, "Other", "", "", 99)
    return products
        .groupBy {
            when {
                it.stockStatusOverride == StockStatus.OUT_OF_STOCK && it.stockStatus == StockStatus.IN_STOCK ->
                    availableInOtherSizes
                !it.inStock -> outOfStockGroup
                it.tags.isNotEmpty() -> it.tags.first()
                else -> defaultGroup
            }
        }.toSortedMap(compareBy { it.sortOrder })
}

/**
 * Builds a Size filter chip group from unique recognized [ProductVariant.sizeCode] values.
 * Preserves [selectedLabels] across catalog refreshes.
 */
internal fun sizeFilterTagType(
    products: List<Product>,
    selectedLabels: Set<String> = emptySet(),
): TagType? {
    val sizes =
        products
            .asSequence()
            .flatMap { it.variants }
            .mapNotNull { it.sizeCode }
            .distinct()
            .sortedBy { it.ordinal }
            .toList()
    if (sizes.isEmpty()) return null

    val selected = selectedLabels.map { it.uppercase() }.toSet()
    return TagType(
        id = SIZE_FILTER_TYPE_ID,
        label = "Size",
        category = "merch-variant",
        isBrowsable = true,
        sortOrder = 0,
        tags =
            sizes.mapIndexed { index, size ->
                Tag(
                    id = SIZE_FILTER_TAG_ID_BASE - index,
                    label = size.code,
                    description = "",
                    color = "#FF0066",
                    sortOrder = index,
                    isSelected = size.code.uppercase() in selected,
                )
            },
    )
}

internal fun getFilteredProducts(
    products: List<Product>,
    filter: List<Tag>,
): List<Product> {
    if (filter.isEmpty()) {
        return products
    }

    return products
        .mapNotNull { product ->
            val hasRecognizedSizes = product.variants.any { it.sizeCode != null }
            if (!hasRecognizedSizes) {
                return@mapNotNull product
            }
            val availableInSelectedSize =
                product.variants.any { variant ->
                    filter.any { selected ->
                        variantMatchesSizeFilter(variant, selected) &&
                            variant.stockStatus != StockStatus.OUT_OF_STOCK
                    }
                }
            if (availableInSelectedSize) {
                product.copy(stockStatusOverride = StockStatus.IN_STOCK)
            } else {
                null
            }
        }.sortedWith(compareBy({ it.stockStatus }, { it.sortOrder }))
}

internal fun variantMatchesSizeFilter(
    variant: ProductVariant,
    selected: Tag,
): Boolean {
    val size = variant.sizeCode ?: return false
    return size.code.equals(selected.label, ignoreCase = true)
}
