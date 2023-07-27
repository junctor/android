package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.core.local.Product
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun EditableProduct(
    product: Product,
    onQuantityChanged: (Int) -> Unit,
) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
        Row(
            Modifier
                .defaultMinSize(minHeight = 64.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(Modifier.weight(1.0f)) {
                // Title
                Text(product.label, style = MaterialTheme.typography.labelLarge)
                // Variant
                if (product.selectedOption != null) {
                    Text(product.selectedOption!!, style = MaterialTheme.typography.labelLarge)
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
                    AsyncImage(model = product.media.first().url, contentDescription = null)
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
                Text(
                    "$${String.format("%.2f", product.cost / 100f)} USD",
                    style = MaterialTheme.typography.titleMedium,
                )
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
                variants = emptyList(),
            ),
            onQuantityChanged = {}
        )
    }
}
