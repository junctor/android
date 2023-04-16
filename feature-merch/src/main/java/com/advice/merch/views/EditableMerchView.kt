package com.advice.merch.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.core.local.Merch
import com.advice.merch.R


@Composable
fun EditableMerchItem(
    merch: Merch,
    onQuantityChanged: (Int) -> Unit,
) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(
            Modifier
                .defaultMinSize(minHeight = 64.dp)
        ) {
            Column(Modifier.weight(1.0f)) {
                val title = merch.label + if(merch.selectedOption != null) " (${merch.selectedOption})" else ""

                Text(
                    title,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            if (merch.image != null) {
                Box(
                    Modifier
                        .background(Color.White)
                        .size(48.dp)
                ) {
                    AsyncImage(model = merch.image, contentDescription = null)
                }
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuantityView(merch.quantity, onQuantityChanged, canDelete = true)
            Column {
                val hasDiscount = merch.discountedPrice != null
                Text(
                    "$${String.format("%.2f", merch.cost/100f)} USD",
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = if (hasDiscount) TextDecoration.LineThrough else TextDecoration.None
                )
                if (hasDiscount) {
                    Text(
                        "$${String.format("%.2f", merch.discountedPrice!!/100f)} USD",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}