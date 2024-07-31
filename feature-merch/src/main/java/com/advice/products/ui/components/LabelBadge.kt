package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.ui.theme.roundedCornerShape

@Composable
internal fun PriceLabel(
    text: String,
) {
    LabelBadge(
        text = text,
        backgroundColor = Color.Black.copy(alpha = 0.5f),
    )
}

@Composable
internal fun LabelBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    backgroundColor: Color = Color.Black,
) {
    Text(
        text = text,
        modifier = modifier
            .background(backgroundColor, shape = roundedCornerShape)
            .padding(horizontal = 8.dp, vertical = 0.dp),
        color = color,
        fontSize = 12.sp,
    )
}
