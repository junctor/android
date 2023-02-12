package com.advice.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.advice.schedule.models.local.Speaker
import com.advice.ui.views.SpeakerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakersScreenView(speakers: List<Speaker>) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Speakers") }, navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.ArrowBack, null)
            }
        })
    }) {
        SpeakersScreenContent(speakers, modifier = Modifier.padding(it))

    }
}

@Composable
fun SpeakersScreenContent(speakers: List<Speaker>, modifier: Modifier = Modifier) {
    LazyColumn(modifier) {
        items(speakers) {
            SpeakerView(it.name, it.title)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpeakersScreenViewPreview() {
    MaterialTheme {
        SpeakersScreenView(listOf(Speaker(-1, "John", "", "", "", "")))
    }
}