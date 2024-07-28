package com.advice.products.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductMedia
import com.advice.core.local.products.ProductVariant
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun ProductsRow(
    products: List<Product>,
    onProductClicked: (Product) -> Unit,
) {
    Row {
        for (product in products) {
            ProductSquare(product, onProductClicked, Modifier.weight(1f))
        }
        if (products.size == 1) Box(modifier = Modifier.weight(1f))
    }
}

@PreviewLightDark
@Composable
private fun ProductsRowPreview() {
    val variants = listOf(
        ProductVariant(1, "S", emptyList(), 0, StockStatus.IN_STOCK),
        ProductVariant(2, "4XL", emptyList(), 0, StockStatus.LOW_STOCK),
        ProductVariant(3, "5XL", emptyList(), 1000, StockStatus.OUT_OF_STOCK)
    )
    val element = Product(
        1L,
        "DC30 Homecoming Men's T-Shirt",
        3500,
        variants,
        quantity = 3,
        media = listOf(
            ProductMedia(
                url = "https://i1.sndcdn.com/artworks-T3fFnZH0gL5eJj0V-zB99zQ-t240x240.jpg",
                sortOrder = 1
            )
        )
    )
    ScheduleTheme {
        Surface {
            Column {
                ProductsRow(
                    products = listOf(element),
                    onProductClicked = {},
                )
            }
        }
    }
}
