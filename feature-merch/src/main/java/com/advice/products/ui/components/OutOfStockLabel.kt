package com.advice.products.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun OutOfStockLabel(modifier: Modifier = Modifier) {
    LabelBadge(
        text = "Out of Stock",
        color = MaterialTheme.colorScheme.onErrorContainer,
        backgroundColor = MaterialTheme.colorScheme.errorContainer,
        modifier = modifier,
    )
}

@PreviewLightDark
@Composable
private fun OutOfStockLabelPreview() {
    ScheduleTheme {
        Surface {
            OutOfStockLabel(
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
