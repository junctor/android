package com.advice.products.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.products.R
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape
import androidx.compose.material3.IconButton as MaterialIconButton

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
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val canDecrement = enabled && (canDelete || quantity > 1)
        val decrementDescription =
            if (canDelete && quantity == 1) {
                stringResource(R.string.cd_remove_from_cart)
            } else {
                stringResource(R.string.cd_decrease_quantity)
            }
        MaterialIconButton(
            onClick = { onQuantityChanged(quantity - 1) },
            enabled = canDecrement,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter =
                    painterResource(
                        id = if (canDelete && quantity == 1) R.drawable.ic_delete else R.drawable.ic_remove,
                    ),
                contentDescription = decrementDescription,
                tint = iconButtonForegroundColor.copy(alpha = if (canDecrement) 1f else 0.5f),
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            quantity.toString(),
            Modifier.defaultMinSize(minWidth = 24.dp),
            color = iconButtonForegroundColor,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
        )
        MaterialIconButton(
            onClick = { onQuantityChanged(quantity + 1) },
            enabled = enabled,
            modifier = Modifier.size(48.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_add),
                contentDescription = stringResource(R.string.cd_increase_quantity),
                tint = iconButtonForegroundColor.copy(alpha = if (enabled) 1f else 0.5f),
                modifier = Modifier.size(18.dp),
            )
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
