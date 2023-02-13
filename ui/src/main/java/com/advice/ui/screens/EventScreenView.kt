package com.advice.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.local.Event
import com.advice.ui.views.ActionView
import com.advice.ui.views.CategoryView
import com.advice.ui.views.SpeakerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreenView(event: Event) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(event.title) }, navigationIcon = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Star, null)
                    }
                })
        }) { contentPadding ->
        EventScreenContent(event, modifier = Modifier.padding(contentPadding))
    }
}

@Composable
fun EventScreenContent(event: Event, modifier: Modifier = Modifier) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)

    ) {
        HeaderSection(event.title, event.types, event.date.toString(), event.location.name)
        Text(
            event.description,
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Card {
            for (action in event.urls) {
                ActionView(action.label)
            }
        }
        if (event.speakers.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Speakers", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            for (speaker in event.speakers) {
                SpeakerView(speaker.name, speaker.title) {

                }
            }
        }
    }
}

@Composable
fun HeaderSection(title: String, categories: List<FirebaseTag>, date: String, location: String) {
    Column {
//        Text(title, style = MaterialTheme.typography.displayLarge)
//        Spacer(Modifier.height(8.dp))
        Row(Modifier.padding(16.dp)) {
            for (tag in categories) {
                CategoryView(tag)
            }
        }
        DetailsCard(date)
        DetailsCard("Tap the location to show all events from this location", isDismissible = true)
        DetailsCard(location)
    }
}

@Composable
private fun DetailsCard(text: String, isDismissible: Boolean = false) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Default.Star, null)
            Spacer(Modifier.width(4.dp))
            Text(text, modifier = Modifier.weight(1f))
            if (isDismissible) {
                Icon(Icons.Default.Close, null, modifier = Modifier.clickable {
                    // todo: dismiss the card
                })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventScreenPreview() {
    MaterialTheme {
        //EventScreenContent(Event(id = -1, conference = "DEFCON30", title = "Arcade Party", _description = "", start = Timestamp.now()))
    }
}