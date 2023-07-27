package com.advice.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun ProductCard(
    modifier: Modifier = Modifier,
    media: String? = null,
    onMerchClick: () -> Unit,
) {
    HomeCard {
        Column(
            modifier = Modifier.clickable {
                onMerchClick()
            }
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (media != null) {
                    AsyncImage(
                        model = media,
                        contentDescription = "logo",
                        modifier = Modifier
                            .background(Color.White)
                            .aspectRatio(2f),
                        contentScale = ContentScale.Crop,
                    )
                } else {

                    val colors = listOf(
                        Color(0xFFEABEBE),
                        Color(0xFFBABEEA),
                    )
                    val gradient = Brush.verticalGradient(colors)

                    Box(
                        modifier = modifier
                            .aspectRatio(2f)
                            .background(
                                gradient // Gradient background
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Empty or additional content as needed
                    }
                }
            }

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    "Browse Merch",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    "Browse the DEF CON 31 merch store",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun MerchCardViewPreview() {
    ScheduleTheme {
        ProductCard(
            media = "https://htem2.habemusconferencing.net/temp/dc24front.jpg",
        ) {
        }
    }
}
