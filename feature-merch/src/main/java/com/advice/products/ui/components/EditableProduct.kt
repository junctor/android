package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.preview.ProductsProvider
import com.advice.products.utils.toCurrency
import com.advice.ui.components.Image
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun EditableProduct(
    product: Product,
    onQuantityChanged: (Int) -> Unit,
) {
    Box {
        val inStock = product.variant?.stockStatus != StockStatus.OUT_OF_STOCK
        Column(
            Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .alpha(if (inStock) 1.0f else 0.25f)
        ) {
            Row(
                Modifier
                    .defaultMinSize(minHeight = 64.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(Modifier.weight(1.0f)) {
                    Text(product.label, style = MaterialTheme.typography.labelLarge)

                    val variant = product.variant
                    if (variant != null) {
                        Text(variant.label, style = MaterialTheme.typography.labelMedium)
                    }

                    // Stock status
                    if (variant?.stockStatus == StockStatus.LOW_STOCK) {
                        Spacer(Modifier.height(4.dp))
                        LowStockLabel()
                    }
                }

                // Image
                if (product.media.isNotEmpty()) {
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .size(64.dp)
                    ) {
                        Image(
                            model = product.media.first().url,
                            contentDescription = product.label,
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuantityAdjuster(
                    product.quantity,
                    onQuantityChanged,
                    canDelete = true,
                    enabled = inStock
                )
                Text(product.cost.toCurrency(), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun EditableProductPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        EditableProduct(
            product = state.products.first(),
            onQuantityChanged = {},
        )
    }
}
