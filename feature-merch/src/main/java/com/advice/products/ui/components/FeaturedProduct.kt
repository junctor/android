package com.advice.products.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.core.local.products.Product
import com.advice.products.utils.toCurrency
import com.advice.ui.preview.LightDarkPreview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FeaturedProducts(products: List<Product>, onProductClicked: (Product) -> Unit) {
    val state = rememberPagerState {
        products.size
    }
    HorizontalPager(state = state) {
        FeaturedProduct(products[it], onProductClicked)
    }
}

@Composable
private fun FeaturedProduct(product: Product, onProductClicked: (Product) -> Unit) {
    Column(modifier = Modifier.clickable {
        onProductClicked(product)
    }) {
        Box(
            Modifier
                .aspectRatio(0.909f)
                .background(Color.Black)
        ) {
            AsyncImage(
                model = product.media.first().url,
                contentDescription = product.label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Column(Modifier.padding(16.dp)) {
            Text(
                product.label, fontWeight = FontWeight.SemiBold,
            )
            Text(product.baseCost.toCurrency())
        }
    }
}

@LightDarkPreview
@Composable
private fun FeaturedProductsPreview() {

}