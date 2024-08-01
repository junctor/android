package com.advice.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.advice.ui.components.BackButton
import com.advice.ui.components.EmptyMessage
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorScreen(message: String = "Something went wrong", onBackPress: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Error") }, navigationIcon = {
                BackButton(onClick = onBackPress)
            })
        },
    ) { contentPadding ->
        EmptyMessage(
            message, modifier = Modifier
                .padding(contentPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenViewPreview() {
    ScheduleTheme {
        ErrorScreen(
            message = "Could not load data",
            onBackPress = {},
        )
    }
}
