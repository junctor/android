package com.advice.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val articles = listOf("Welcome to DEFCON 28")

val roundedCornerShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenView() {
    Scaffold(topBar = { TopAppBar(title = { Text("Home") }) }, modifier = Modifier.clip(roundedCornerShape)) { contentPadding ->
        HomeScreenContent(modifier = Modifier.padding(contentPadding))
    }
}

@Composable
fun HomeScreenContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        ConferenceView()
        CountdownView()
        LazyColumn {
            items(articles) {
                ArticleView(text = it)
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenViewPreview() {
    MaterialTheme {
        HomeScreenView()
    }
}

@Composable
fun ConferenceView() {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(48.dp))
            Text("DEFCON 28")
        }
    }
}

@Composable
fun CountdownView() {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("76 days")
            Text("05 hours")
            Text("56 minutes")
            Text("08 seconds")
        }
    }
}

@Composable
fun ArticleView(text: String) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Text(text, modifier = Modifier.padding(16.dp))
    }
}

@Preview
@Composable
fun ConferenceViewPreview() {
    MaterialTheme {
        ConferenceView()
    }
}

@Preview
@Composable
fun CountdownViewPreview() {
    MaterialTheme {
        CountdownView()
    }
}

@Preview
@Composable
fun ArticleViewPreview() {
    MaterialTheme {
        ArticleView("Welcome to DEFCON 28!")
    }
}