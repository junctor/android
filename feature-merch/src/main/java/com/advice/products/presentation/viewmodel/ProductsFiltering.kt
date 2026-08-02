package com.advice.products.presentation.viewmodel

import com.advice.core.local.StockStatus
import com.advice.core.local.Tag
import com.advice.core.local.products.Product

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

internal fun getFilteredProducts(
    products: List<Product>,
    filter: List<Tag>,
): List<Product> {
    if (filter.isEmpty()) {
        return products
    }

    return products
        .map { product ->
            if (!product.requiresSelection) {
                return@map product
            }
            val inStock =
                product.variants.any { variant ->
                    filter.any { it.id in variant.tags && variant.stockStatus == StockStatus.IN_STOCK }
                }
            if (inStock) {
                product.copy(stockStatusOverride = StockStatus.IN_STOCK)
            } else {
                product.copy(stockStatusOverride = StockStatus.OUT_OF_STOCK)
            }
        }.sortedWith(compareBy({ it.stockStatus }, { it.sortOrder }))
}
