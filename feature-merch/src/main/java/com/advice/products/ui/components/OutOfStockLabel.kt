package com.advice.products.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun OutOfStockLabel(modifier: Modifier = Modifier) {
    LabelBadge(
        text = "Out of Stock",
        backgroundColor = MaterialTheme.colorScheme.errorContainer,
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun OutOfStockLabelPreview() {
    ScheduleTheme {
        OutOfStockLabel()
    }
}
