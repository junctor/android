package com.advice.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.ui.views.FilterView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreenView() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Filter") }, actions = {
                Icon(Icons.Default.Close, null)
            })
        },
        modifier = Modifier.clip(RoundedCornerShape(16.dp))
    ) { contentPadding ->
        FilterScreenContent(Modifier.padding(contentPadding))
    }
}

@Composable
fun FilterScreenContent(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(10) {
            FilterView()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterScreenViewPreview() {
    MaterialTheme {
        FilterScreenView()
    }
}