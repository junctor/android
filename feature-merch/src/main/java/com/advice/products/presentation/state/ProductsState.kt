package com.advice.products.presentation.state

import com.advice.core.local.Product

data class ProductsState(
    val elements: List<Product>,
    val canAdd: Boolean = false,
    val cart: List<Product> = emptyList(),
    val json: String? = null,
)