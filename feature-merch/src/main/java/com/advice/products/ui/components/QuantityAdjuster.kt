package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.products.R
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape

@Composable
fun QuantityAdjuster(
    quantity: Int,
    onQuantityChanged: (Int) -> Unit,
    canDelete: Boolean,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier
            .background(iconButtonBackgroundColor, roundedCornerShape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val canDecrement = enabled && (canDelete || quantity > 1)
        QualityButton(
            onQuantityChanged = { onQuantityChanged(quantity - 1) },
            quantity = quantity,
            enabled = canDecrement
        ) {
            Icon(
                painter = painterResource(id = if (canDelete && quantity == 1) R.drawable.ic_delete else R.drawable.ic_remove),
                contentDescription = "Decrease quantity",
                tint = iconButtonForegroundColor.copy(alpha = if (canDecrement) 1f else 0.5f),
            )
        }
        Text(
            quantity.toString(),
            Modifier.defaultMinSize(minWidth = 24.dp),
            color = iconButtonForegroundColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        QualityButton(
            onQuantityChanged = { onQuantityChanged(quantity + 1) },
            quantity = quantity,
            enabled = enabled,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = "Increase quantity",
                tint = iconButtonForegroundColor,
            )
        }
    }
}

@Composable
private fun QualityButton(
    onQuantityChanged: (Int) -> Unit,
    quantity: Int,
    enabled: Boolean,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clickable(enabled = enabled) { onQuantityChanged(quantity) },
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .align(Alignment.Center)
        ) {
            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun QuantityViewPreview() {
    ScheduleTheme {
        Surface {
            QuantityAdjuster(
                quantity = 1,
                onQuantityChanged = {},
                canDelete = true,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun QuantityViewMultipleQuantityPreview() {
    ScheduleTheme {
        Surface {
            QuantityAdjuster(
                quantity = 7,
                onQuantityChanged = {},
                canDelete = true,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun QuantityViewDisabledDeletePreview() {
    ScheduleTheme {
        Surface {
            QuantityAdjuster(
                quantity = 1,
                onQuantityChanged = {},
                canDelete = false,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

