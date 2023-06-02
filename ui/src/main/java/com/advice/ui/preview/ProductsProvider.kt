package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Product
import com.advice.core.local.ProductVariant
import com.advice.core.local.ProductMedia
import com.advice.core.ui.ProductsState

class ProductsProvider : PreviewParameterProvider<ProductsState> {
    override val values: Sequence<ProductsState>
        get() {
            val options = listOf(
                ProductVariant("S", true, 0),
                ProductVariant("4XL", true, 0),
                ProductVariant("5XL", false, 1000)
            )
            val product = Product(
                -1L, "DC30 Homecoming Men's T-Shirt", 35_00, options, media = listOf(
                    ProductMedia(
                        "https://htem2.habemusconferencing.net/temp/dc24front.jpg",
                        0
                    )
                )
            )
            return listOf(
                ProductsState(
                    elements = listOf(product),
                    hasDiscount = true,
                )
            ).asSequence()
        }

}