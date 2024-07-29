package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
            .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        QualityButton(
            onQuantityChanged = { onQuantityChanged(quantity - 1) },
            quantity = quantity,
            enabled = enabled && (canDelete || quantity > 1)
        ) {
            Icon(
                painterResource(if (canDelete && quantity == 1) R.drawable.ic_delete else R.drawable.ic_remove),
                null,
                tint = MaterialTheme.colorScheme.surface.copy(alpha = if (!enabled) 0.5f else 1.0f),
            )
        }
        Text(
            quantity.toString(),
            Modifier.defaultMinSize(minWidth = 48.dp),
            color = MaterialTheme.colorScheme.surface,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )
        QualityButton(
            onQuantityChanged = { onQuantityChanged(quantity + 1) },
            quantity = quantity,
            enabled = enabled,
        ) {
            Icon(
                painterResource(R.drawable.ic_add), null,
                tint = MaterialTheme.colorScheme.surface
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
    IconButton(
        onClick = { onQuantityChanged(quantity) },
        modifier = Modifier.size(14.dp),
        enabled = enabled
    ) {
        content()
    }
}

@PreviewLightDark
@Composable
private fun QuantityViewPreview() {
    ScheduleTheme {
        QuantityAdjuster(1, {}, canDelete = true)
    }
}
