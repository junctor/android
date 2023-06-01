@file:OptIn(ExperimentalLayoutApi::class, ExperimentalLayoutApi::class)

package com.advice.merch.views

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
import com.advice.core.local.Merch
import com.advice.core.local.ProductMedia
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MerchView(merch: Merch, onMerchClicked: (Merch) -> Unit) {
    Row(
        Modifier
            .clickable { onMerchClicked(merch) }
            .defaultMinSize(minHeight = 86.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(Modifier.weight(1.0f)) {
            Text(merch.label, style = MaterialTheme.typography.labelLarge)
            Text(
                "$${String.format("%.2f", merch.baseCost / 100f)} USD",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))
            FlowRow(
                Modifier.fillMaxWidth(),
            ) {
                for ((index, option) in merch.options.withIndex()) {
                    MerchOption(option.label, inStock = option.inStock)
                }
            }
        }
        BadgedBox(
            badge = {
                if (merch.quantity > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = -12.dp, y = 12.dp)
                    ) {
                        Text(
                            "${merch.quantity}",
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
            if (merch.media.isNotEmpty()) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                ) {
                    AsyncImage(model = merch.media.first().url, contentDescription = null)
                }
            }
        }
    }
}

@LightDarkPreview
@Composable
fun MerchItemPreview() {
    val options = listOf(
        com.advice.core.local.MerchOption("S", true, 0),
        com.advice.core.local.MerchOption("4XL", true, 0),
        com.advice.core.local.MerchOption("5XL", false, 1000)
    )
    val element = Merch(
        1L,
        "DC30 Homecoming Men's T-Shirt",
        3500,
        options,
        quantity = 3,
        media = listOf(
            ProductMedia(
                "https://i1.sndcdn.com/artworks-T3fFnZH0gL5eJj0V-zB99zQ-t240x240.jpg",
                1
            )
        )
    )
    ScheduleTheme {
        MerchView(element) {}
    }
}