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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductVariant
import com.advice.products.utils.toCurrency
import com.advice.ui.preview.LightDarkPreview
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
                .alpha(if (inStock) 1.0f else 0.5f)
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
                        AsyncImage(
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
                QuantityAdjuster(product.quantity, onQuantityChanged, canDelete = true)
                Column {
                    Text(product.cost.toCurrency(), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun EditableProductPreview() {
    ScheduleTheme {
        EditableProduct(
            Product(
                id = 1,
                label = "T-Shirt",
                baseCost = 1000,
                cost = 1000,
                quantity = 1,
                selectedOption = "M",
                media = emptyList(),
                variants = listOf(
                    ProductVariant(
                        -1,
                        "M",
                        emptyList(),
                        10_00,
                        StockStatus.LOW_STOCK
                    )
                ),
            ),
            onQuantityChanged = {}
        )
    }
}
