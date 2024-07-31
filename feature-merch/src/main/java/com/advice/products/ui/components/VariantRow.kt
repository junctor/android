package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.StockStatus
import com.advice.core.local.products.ProductVariant
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.preview.ProductsProvider
import com.advice.products.utils.toCurrency
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun VariantRow(
    variant: ProductVariant,
    isSelected: Boolean,
    onSelect: () -> Unit,
    canAdd: Boolean = false,
) {
    val inStock = variant.stockStatus != StockStatus.OUT_OF_STOCK
    Row(
        Modifier
            .defaultMinSize(minHeight = 42.dp)
            .clickable(enabled = inStock, onClick = onSelect)

            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .alpha(if (inStock) 1.0f else 0.50f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = variant.label,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (inStock) {
                LabelBadge(
                    text = variant.price.toCurrency(showCents = true),
                )
            }
        }

        if (variant.stockStatus == StockStatus.LOW_STOCK) {
            Spacer(modifier = Modifier.width(4.dp))
            LowStockLabel()
        }
        if (variant.stockStatus == StockStatus.OUT_OF_STOCK) {
            Spacer(modifier = Modifier.width(4.dp))
            OutOfStockLabel()
        }
        if (canAdd && inStock) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
            )
        } else {
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@PreviewLightDark
@Composable
private fun VariantRowPreview(
    @PreviewParameter(ProductsProvider::class) state: ProductsState,
) {
    ScheduleTheme {
        val variants = state.products.first().variants
        Column(Modifier.background(MaterialTheme.colorScheme.background)) {
            for (variant in variants) {
                VariantRow(
                    variant = variant,
                    isSelected = false,
                    onSelect = {},
                    canAdd = true,
                )
            }
        }
    }
}
