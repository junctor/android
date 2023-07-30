package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.advice.core.local.products.Product
import com.advice.ui.preview.LightDarkPreview

@Composable
fun FeaturedProducts(products: List<Product>, onProductClicked: (Product) -> Unit) {
    Row {
        products.forEach { product ->
            Box(Modifier.aspectRatio(2.3f).background(Color.Red)) {
                AsyncImage(
                    model = product.media.first().url,
                    contentDescription = product.label,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun FeaturedProductsPreview() {

}