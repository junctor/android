package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
    product: Product,
    onMerchClicked: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .aspectRatio(.909f)
            .padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        Box(
            Modifier
                .background(Color.Black, shape = RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable { onMerchClicked(product) },
        ) {
            Image(
                model = product.media.firstOrNull()?.url,
                contentDescription = product.label,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (product.inStock) 1f else 0.5f),
                contentScale = ContentScale.Crop,
            )

            LabelBadge(
                text = product.code,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp),
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (product.stockStatus == StockStatus.LOW_STOCK) {
                    LabelBadge(
                        text = "Low Stock",
                        backgroundColor = MaterialTheme.colorScheme.errorContainer,
                    )
                }

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
            }
        }

        if (product.quantity > 0) {
            LabelBadge(
                text = "Added",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                backgroundColor = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun LabelBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    backgroundColor: Color = Color.Black,
) {
    Text(
        text = text,
        modifier = modifier
            .background(backgroundColor.copy(alpha = 0.50f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 0.dp),
        color = color,
        fontSize = 12.sp,
    )
}

@PreviewLightDark
@Composable
private fun InStockProductSquarePreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        Surface {
            ProductSquare(
                product = state.products.find { it.stockStatus == StockStatus.IN_STOCK }!!,
                onMerchClicked = {},
                modifier = Modifier.size(300.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun OutOfStockProductSquarePreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        Surface {
            ProductSquare(
                product = state.products.find { it.stockStatus == StockStatus.OUT_OF_STOCK }!!,
                onMerchClicked = {},
                modifier = Modifier.size(300.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LowStockProductSquarePreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        Surface {
            ProductSquare(
                product = state.products.find { it.stockStatus == StockStatus.LOW_STOCK }!!,
                onMerchClicked = {},
                modifier = Modifier.size(300.dp),
            )
        }
    }
}
