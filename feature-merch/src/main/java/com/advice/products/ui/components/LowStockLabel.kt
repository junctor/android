package com.advice.products.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.advice.products.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun LowStockLabel(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Badge(containerColor = MaterialTheme.colorScheme.errorContainer) {
            Text(stringResource(R.string.badge_alert))
        }
        Text(
            stringResource(R.string.low_stock_message),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
