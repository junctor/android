package com.advice.products.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Product
import com.advice.core.local.ProductMedia
import com.advice.core.local.ProductVariant
import com.advice.products.presentation.state.ProductsState
import com.advice.products.utils.toJson

class ProductsProvider : PreviewParameterProvider<ProductsState> {
    override val values: Sequence<ProductsState>
        get() {
            val options = listOf(
                ProductVariant("S", emptyList(), 0),
                ProductVariant("4XL", emptyList(), 0),
                ProductVariant("5XL", emptyList(), 1000)
            )
            val product = Product(
                -1L, "DC30 Homecoming Men's T-Shirt", 35_00, options,
                media = listOf(
                    ProductMedia(
                        "https://htem2.habemusconferencing.net/temp/dc24front.jpg",
                        0
                    )
                ),
                quantity = 1,
                selectedOption = options[0].label,
            )

            val elements = listOf(product)

            return listOf(
                ProductsState(
                    elements = elements,
                    cart = elements,
                    json = elements.toJson(),
                )
            ).asSequence()
        }
}
