package com.advice.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeOfConductScreenView(policy: String?, onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Code of Conduct") },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }) {
        if (policy != null) {
            Text(
                policy, modifier = Modifier
                    .padding(it)
                    .padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CodeOfConductScreenViewPreview() {
    MaterialTheme {
        CodeOfConductScreenView("Code of Conduct policy, email us at support@gmail.com") {}
    }
}