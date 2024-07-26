package com.advice.products.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark

@Composable
internal fun LegalLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )
}

@PreviewLightDark
@Composable
private fun LegalLabelPreview() {
    LegalLabel("All sales are **CASH ONLY**. Prices include Nevada State Sales Tax.")
}
