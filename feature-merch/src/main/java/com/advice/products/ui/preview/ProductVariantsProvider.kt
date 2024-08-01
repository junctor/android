package com.advice.products.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.StockStatus
import com.advice.core.local.products.ProductVariant

class ProductVariantsProvider : PreviewParameterProvider<List<ProductVariant>> {
    override val values: Sequence<List<ProductVariant>>
        get() {
            val variants = listOf(
                ProductVariant(1, "S", emptyList(), 35_00, StockStatus.IN_STOCK),
                ProductVariant(2, "4XL", emptyList(), 40_00, StockStatus.LOW_STOCK),
                ProductVariant(3, "5XL", emptyList(), 45_00, StockStatus.OUT_OF_STOCK)
            )

            return listOf(variants).asSequence()
        }
}
