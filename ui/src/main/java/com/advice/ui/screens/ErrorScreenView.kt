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
import com.advice.ui.views.EmptyView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorScreenView(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Error") }, navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            })
        }) { contentPadding ->
        EmptyView(modifier = Modifier.padding(contentPadding))
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenViewPreview() {
    MaterialTheme {
        ErrorScreenView() {

        }
    }
}