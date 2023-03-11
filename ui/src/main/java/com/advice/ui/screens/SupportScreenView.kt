package com.advice.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.views.Paragraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreenView(
    message: String?,
    onBackPressed: () -> Unit,
    onLinkClicked: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & Support") },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }) {
        if (message != null) {
            Paragraph(
                message,
                Modifier.padding(it),
                onLinkClicked
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SupportScreenViewPreview() {
    ScheduleTheme {
        SupportScreenView("If you need support, please call us 555-555-0000", {}, {})
    }
}