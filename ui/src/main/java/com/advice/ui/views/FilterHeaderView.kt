package com.advice.ui.views

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FilterHeaderView(title: String) {
    Text(title, modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true)
@Composable
fun FilterHeaderViewPreview() {
    MaterialTheme {
        FilterHeaderView("General")
    }
}