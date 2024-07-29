package com.advice.products.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun LowStockLabel(modifier: Modifier = Modifier) {
    LabelBadge(
        text = "Low Stock",
        backgroundColor = MaterialTheme.colorScheme.errorContainer,
        modifier = modifier,
    )
}
