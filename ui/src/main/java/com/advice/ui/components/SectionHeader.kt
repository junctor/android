package com.advice.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun SectionHeader(title: String) {
    Label(title, modifier = Modifier.fillMaxWidth())
}

@Preview(showBackground = true)
@Composable
private fun SectionHeaderPreview() {
    ScheduleTheme {
        SectionHeader("General")
    }
}