package com.advice.core.local.products

data class ProductSelection(
    val product: Product,
    val variant: ProductVariant,
    val quantity: Int,
) {
    val id: Long
        get() = product.id

    val cost: Long
        get() = variant.price * quantity

    val label: String
        get() = product.label

    val media: List<ProductMedia>
        get() = product.media
}
