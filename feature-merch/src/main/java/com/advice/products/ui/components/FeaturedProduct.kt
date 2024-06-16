package com.advice.products.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.preview.ProductsProvider
import com.advice.products.utils.toCurrency
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeaturedProducts(
    products: List<Product>,
    onProductClicked: (Product) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val state = rememberPagerState {
        products.size
    }

    LaunchedEffect(Unit) {
        scope.launch {
            while (true) {
                delay(4000L)
                with(state) {
                    animateScrollToPage((currentPage + 1) % pageCount)
                }
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(state = state) {
            FeaturedProduct(products[it], onProductClicked)
        }
        PagerDots(state)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerDots(
    state: PagerState,
    modifier: Modifier = Modifier,
) {
    // Don't show dots if there is only one page
    if (state.pageCount < 2) return

    Row(modifier.padding(8.dp)) {
        for (i in 0 until state.pageCount) {
            Box(
                Modifier
                    .padding(4.dp)
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (state.currentPage == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                    ),
            )
        }
    }
}

@Composable
private fun FeaturedProduct(
    product: Product,
    onProductClicked: (Product) -> Unit,
) {
    Column(
        modifier = Modifier.clickable {
            onProductClicked(product)
        },
    ) {
        Box(
            Modifier
                .aspectRatio(0.9f)
                .background(Color.Black),
        ) {
            AsyncImage(
                model = product.media.first().url,
                contentDescription = product.label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
            ) {
                when (product.stockStatus) {
                    StockStatus.IN_STOCK -> {
                        // no-op
                    }

                    StockStatus.LOW_STOCK -> {
                        LowStockLabel()
                    }

                    StockStatus.OUT_OF_STOCK -> {
                        OutOfStockLabel()
                    }
                }
            }
        }
        Column(Modifier.padding(16.dp)) {
            Text(
                product.label,
                fontWeight = FontWeight.SemiBold,
            )
            Text(product.baseCost.toCurrency())
        }
    }
}

@PreviewLightDark
@Composable
private fun FeaturedProductsPreview(
    @PreviewParameter(ProductsProvider::class) state: ProductsState,
) {
    ScheduleTheme {
        FeaturedProducts(
            products = state.products,
            onProductClicked = {
            },
        )
    }
}
