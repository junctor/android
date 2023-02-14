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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.local.Event
import com.advice.ui.views.ActionView
import com.advice.ui.views.BookmarkButton
import com.advice.ui.views.CategoryView
import com.advice.ui.views.NoDetailsView
import com.advice.ui.views.SpeakerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreenView(event: Event, onBookmark: () -> Unit, onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(event.title, maxLines = 2, overflow = TextOverflow.Ellipsis) }, navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            },
                actions = {
                    BookmarkButton(isBookmarked = event.isBookmarked) {
                        onBookmark()
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
        if (event.description.isNotBlank()) {
            Text(
                event.description,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        Card {
            for (action in event.urls) {
                ActionView(action.label)
            }
        }
        if (event.speakers.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Speakers", textAlign = TextAlign.Center, modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            for (speaker in event.speakers) {
                SpeakerView(speaker.name, speaker.title) {

                }
            }
        }

        if (event.description.isBlank() && event.urls.isEmpty() && event.speakers.isEmpty()) {
            NoDetailsView()
        }
    }
}

@Composable
fun HeaderSection(title: String, categories: List<FirebaseTag>, date: String, location: String) {
    Column {
        Row(Modifier.padding(16.dp)) {
            for (tag in categories) {
                CategoryView(tag)
            }
        }
        DetailsCard(Icons.Default.DateRange, date)
        DetailsCard(Icons.Default.Info, "Tap the location to show all events from this location") {
            // dismiss
        }
        DetailsCard(Icons.Default.LocationOn, location)
    }
}

@Composable
private fun DetailsCard(icon: ImageVector, text: String, onDismiss: (() -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(icon, null)
            Spacer(Modifier.width(8.dp))
            Text(text, modifier = Modifier.weight(1f))
            if (onDismiss != null) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null)
                }
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