package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.local.products.ProductMedia
import com.advice.ui.components.Image
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun ImageGallery(media: List<ProductMedia>) {
    val aspectRatio = 0.90909f

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        val imageWidth =
            with(LocalDensity.current) { (maxWidth - 32.dp).toPx() }

        val containerHeight = with(LocalDensity.current) { (maxWidth - 16.dp * aspectRatio).toPx() }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(with(LocalDensity.current) { containerHeight.toDp() })
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            for (image in media) {
                Image(
                    model = image.url,
                    contentDescription = "product image",
                    modifier = Modifier
                        .width(with(LocalDensity.current) { imageWidth.toDp() })
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black),
                    contentScale = ContentScale.Crop,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Preview
@Composable
private fun ImageGalleryPreview() {
    ScheduleTheme {
        Surface(Modifier.background(Color.Red)) {
            ImageGallery(
                listOf(
                    ProductMedia("https://via.placeholder.com/150"),
                )
            )
        }
    }
}

@Preview
@Composable
private fun ImageGalleryMultiplePreview() {
    ScheduleTheme {
        Surface(Modifier.background(Color.Red)) {
            ImageGallery(
                listOf(
                    ProductMedia("https://via.placeholder.com/150"),
                    ProductMedia("https://via.placeholder.com/150"),
                )
            )
        }
    }
}
