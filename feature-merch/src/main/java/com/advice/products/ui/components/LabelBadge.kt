package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun LabelBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    backgroundColor: Color = Color.Black,
) {
    Text(
        text = text,
        modifier = modifier
            .background(backgroundColor.copy(alpha = 0.50f), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 0.dp),
        color = color,
        fontSize = 12.sp,
    )
}
