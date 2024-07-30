package com.advice.products.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun LowStockLabel(modifier: Modifier = Modifier) {
    val color = Color(0xFF304FFE)
    LabelBadge(
        text = "Low Stock",
        modifier = modifier,
        color = Color.White,
        backgroundColor = color,
    )
}

@PreviewLightDark
@Composable
private fun LowStockLabelPreview() {
    ScheduleTheme {
        Surface {
            LowStockLabel(modifier = Modifier.padding(16.dp))
        }
    }
}
