package com.advice.merch.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.advice.core.local.Merch
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchView(merch: Merch, onMerchClicked: (Merch) -> Unit) {
    Row(
        Modifier
            .clickable { onMerchClicked(merch) }
            .defaultMinSize(minHeight = 86.dp)
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Column(Modifier.weight(1.0f)) {
            Text(merch.label, style = MaterialTheme.typography.labelLarge)
            Text(
                "$${String.format("%.2f", merch.baseCost / 100f)} USD",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.fillMaxWidth(),
            ) {
                for ((index, option) in merch.options.withIndex()) {
                    // todo: get in stock from backend
                    MerchOption(option.label, inStock = option.inStock)
                    if (index != merch.options.size - 1) {
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }
        }
        BadgedBox(badge = {
            if (merch.quantity > 0) {
                Badge(containerColor = MaterialTheme.colorScheme.primary, modifier = Modifier.offset(x = -8.dp, y = 8.dp)) {
                    Text(
                        "${merch.quantity}",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 18.sp,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        },
            Modifier
                .size(64.dp)) {
            if (merch.image != null) {
                Box(
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)

                ) {
                    AsyncImage(model = merch.image, contentDescription = null)
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
        "1",
        "DC30 Homecoming Men's T-Shirt",
        3500,
        options,
        quantity = 3,
        image = "https://i1.sndcdn.com/artworks-T3fFnZH0gL5eJj0V-zB99zQ-t240x240.jpg",
    )
    ScheduleTheme {
        MerchView(element) {}
    }
}