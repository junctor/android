@file:OptIn(ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)

package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductMedia
import com.advice.core.local.products.ProductVariant
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProductView(product: Product, onMerchClicked: (Product) -> Unit) {
    Row(
        Modifier
            .clickable { onMerchClicked(product) }
            .defaultMinSize(minHeight = 86.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(Modifier.weight(1.0f)) {
            Text(product.label, style = MaterialTheme.typography.labelLarge)
            Text(
                "$${String.format("%.2f", product.baseCost / 100f)} USD",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))
            FlowRow(
                Modifier.fillMaxWidth(),
            ) {
                for ((index, option) in product.variants.withIndex()) {
                    // TODO: Add inStock
                    ProductVariantTag(option.label, inStock = true)
                }
            }
        }
        BadgedBox(
            badge = {
                if (product.quantity > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = -12.dp, y = 12.dp)
                    ) {
                        Text(
                            "${product.quantity}",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            },
            Modifier
                .size(64.dp)
        ) {
            if (product.media.isNotEmpty()) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                ) {
                    AsyncImage(model = product.media.first().url, contentDescription = null)
                }
            }
        }
    }
}

@LightDarkPreview
@Composable
fun ProductViewPreview() {
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
                "https://i1.sndcdn.com/artworks-T3fFnZH0gL5eJj0V-zB99zQ-t240x240.jpg",
                1
            )
        )
    )
    ScheduleTheme {
        ProductView(element) {}
    }
}
