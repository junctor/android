package com.advice.core.local.products

data class ProductSelection(
    val id: Long,
    val quantity: Int,
    val variant: ProductVariant?,
)
