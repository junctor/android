package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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


@Composable
fun ProductSquare(
    product: Product, onMerchClicked: (Product) -> Unit, modifier: Modifier = Modifier
) {
    BadgedBox(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 4.dp),
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
    ) {
        Box(
            Modifier
                .clickable { onMerchClicked(product) }
                .aspectRatio(.909f)
                .background(Color.Black, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp)),
        ) {
            Image(
                model = product.media.firstOrNull()?.url,
                contentDescription = product.label,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (product.inStock) 1f else 0.5f),
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (product.stockStatus == StockStatus.OUT_OF_STOCK) {
                    LabelBadge(
                        text = "Out of Stock",
                        backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    )
                } else {
                    LabelBadge(
                        text = product.baseCost.toCurrency(showCents = true),
                    )
                }

                if (product.stockStatus == StockStatus.LOW_STOCK) {
                    LabelBadge(
                        text = "Low Stock",
                        backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    )
                }
            }
        }
    }
}

@Composable
private fun LabelBadge(
    text: String,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    backgroundColor: Color = Color.Black,
) {
    Text(
        text = text,
        modifier = Modifier
            .background(backgroundColor.copy(alpha = 0.50f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 0.dp),
        color = color,
        fontSize = 12.sp,
    )
}

@PreviewLightDark
@Composable
private fun ProductSquarePreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        Surface {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                for (product in state.products) {
                    ProductSquare(product, {})
                }
            }
        }
    }
}
