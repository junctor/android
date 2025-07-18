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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.preview.ProductsProvider
import com.advice.products.utils.toCurrency
import com.advice.ui.components.Image
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductRow(
    product: Product,
    onMerchClicked: (Product) -> Unit,
    quantity: Int = 0,
) {
    Row(
        Modifier
            .clickable { onMerchClicked(product) }
            .defaultMinSize(minHeight = 86.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Column(Modifier.weight(1.0f)) {
            Text(product.label, style = MaterialTheme.typography.labelLarge)
            Text(
                product.baseCost.toCurrency(showCents = true, showPlus = product.hasPriceVariation),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))
            FlowRow(
                Modifier.fillMaxWidth(),
            ) {
                for (option in product.variants) {
                    ProductVariantTag(
                        text = option.label, inStock = option.stockStatus == StockStatus.IN_STOCK
                    )
                }
            }
        }
        BadgedBox(
            badge = {
                if (quantity > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = -12.dp, y = 12.dp)
                    ) {
                        Text(
                            text = "$quantity",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }, Modifier.size(64.dp)
        ) {
            if (product.media.isNotEmpty()) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                ) {
                    Image(
                        model = product.media.first().url,
                        contentDescription = product.label,
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ProductViewPreview(
    @PreviewParameter(ProductsProvider::class) state: ProductsState,
) {
    ScheduleTheme {
        Surface {
            ProductRow(
                product = state.products.first(),
                onMerchClicked = {},
                quantity = 1,
            )
        }
    }
}
