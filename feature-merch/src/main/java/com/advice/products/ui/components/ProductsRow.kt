package com.advice.products.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.advice.core.local.products.Product
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.preview.ProductsProvider
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun ProductsRow(
    products: List<Product>,
    onProductClicked: (Product) -> Unit,
) {
    Row {
        for (product in products) {
            ProductSquare(
                product = product,
                onMerchClicked = onProductClicked,
                modifier = Modifier.weight(1f),
            )
        }
        if (products.size == 1) Box(modifier = Modifier.weight(1f))
    }
}

@PreviewLightDark
@Composable
private fun ProductsRowPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        Surface {
            Column {
                ProductsRow(
                    products = state.products.take(2),
                    onProductClicked = {},
                )
            }
        }
    }
}
