package com.advice.feedback.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.advice.ui.preview.LightDarkPreview

@Composable
internal fun DisplayOnlyItem(value: String) {
    Text(value)
}

@LightDarkPreview
@Composable
private fun DisplayOnlyItemPreview() {
    DisplayOnlyItem("Thank you so much for attending Policy @ DEF CON, and for taking the time to leave feedback.")
}
