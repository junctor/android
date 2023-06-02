package com.advice.core.ui

import com.advice.core.local.Product

data class ProductsState(
    val elements: List<Product>,
    val hasDiscount: Boolean = false
)