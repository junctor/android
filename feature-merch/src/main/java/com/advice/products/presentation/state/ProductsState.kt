package com.advice.products.presentation.state

import com.advice.core.local.products.Product

data class ProductsState(
    val featured: List<Product>,
    val products: List<Product>,
    val merchDocument: Int? = null,
    val showMerchInformation: Boolean = false,
    val canAdd: Boolean = false,
    val cart: List<Product> = emptyList(),
    val json: String? = null,
) {
    companion object {
        val EMPTY = ProductsState(emptyList(), emptyList())
    }
}
