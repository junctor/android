package com.advice.products.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.ui.theme.HotPink
import com.advice.ui.theme.ScheduleTheme

@Composable
fun ProductVariantTag(text: String, inStock: Boolean = true) {
    val label = when (text) {
        "Extra-Small" -> "XS"
        "Small" -> "S"
        "Medium" -> "M"
        "Large" -> "L"
        else -> text
    }

    Text(
        label,
        Modifier
            .padding(4.dp)
            .alpha(if (inStock) 1.0f else 0.60f)
            .border(1.dp, HotPink, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 2.dp),
        color = Color.White,
        fontSize = 14.sp,
    )
}

@Preview
@Composable
private fun MerchOptionPreview() {
    ScheduleTheme {
        ProductVariantTag(text = "4XL")
    }
}
