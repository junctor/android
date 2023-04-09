package com.advice.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.local.Merch
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.shortstack.hackertracker.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchItem(merch: Merch, onMerchClicked: (Merch) -> Unit) {
    BadgedBox(badge = {
        if (merch.count > 0) {
            Badge(containerColor = Color.Red) {
                Text("${merch.count}", style = MaterialTheme.typography.labelLarge)
            }
        }
    }, Modifier
        .clickable { onMerchClicked(merch) }
        .defaultMinSize(minHeight = 86.dp)
        .padding(horizontal = 24.dp, vertical = 8.dp)) {
        Row(
            Modifier
        ) {
            Column(Modifier.weight(1.0f)) {
                Text(merch.label, style = MaterialTheme.typography.labelLarge)
                Text("$${merch.cost} USD", style = MaterialTheme.typography.bodyMedium)

                Row {
                    for (tag in merch.sizes) {
                        Text(
                            tag,
                            Modifier
                                .padding(horizontal = 4.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (merch.image) {
                Box(
                    Modifier
                        .background(Color.White)
                        .size(64.dp)
                ) {
                    Image(painterResource(R.drawable.doggo), null)
                }
            }
        }
    }
}

@LightDarkPreview
@Composable
fun MerchItemPreview() {
    val element = Merch(
        "DC30 Homecoming Men's T-Shirt",
        35,
        listOf("S", "4XL", "5XL", "6XL"),
        image = true,
        count = 3
    )
    ScheduleTheme {
        MerchItem(element) {}
    }
}