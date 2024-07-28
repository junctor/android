package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductMedia
import com.advice.core.local.products.ProductVariant
import com.advice.products.R
import com.advice.products.utils.toCurrency
import com.advice.ui.components.Image
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme


@Composable
fun ProductSquare(
    product: Product,
    onMerchClicked: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .clickable { onMerchClicked(product) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
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
        ) {
            Box(
                Modifier
                    .aspectRatio(.909f)
                    .clip(RoundedCornerShape(8.dp)),
            ) {
                Image(
                    model = product.media.firstOrNull()?.url,
                    contentDescription = product.label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )

                if (product.stockStatus == StockStatus.OUT_OF_STOCK) {
                    OutOfStock()
                }

                if (product.stockStatus == StockStatus.LOW_STOCK) {
                    LowStockBadge()
                }
            }
        }
        Column(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(
                product.label, fontWeight = FontWeight.SemiBold,
            )
            Text(product.baseCost.toCurrency())
        }
    }
}

@Composable
internal fun LowStockBadge() {
    Badge(
        containerColor = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier
            .offset(8.dp, 8.dp)
    ) {
        Text(stringResource(R.string.badge_alert))
    }
}

@Composable
internal fun OutOfStock() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.50f))
            .padding(16.dp),
    ) {
        Text(
            "Out of Stock",
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Black,
        )
    }
}

@PreviewLightDark
@Composable
private fun ProductSquarePreview() {
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
        ProductSquare(element, {})
    }
}
