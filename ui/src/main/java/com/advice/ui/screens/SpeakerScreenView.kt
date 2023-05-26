package com.advice.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Event
import com.advice.core.local.Speaker
import com.advice.core.utils.TimeUtil
import com.advice.ui.preview.SpeakerProvider
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.views.EventRowView
import com.advice.ui.views.NoDetailsView
import com.advice.ui.views.Paragraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakerScreenView(name: String, title: String, description: String, events: List<Event>, onBackPressed: () -> Unit, onEventClicked: (Event) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text(name) }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, null)
            }
        })
    }) {
        SpeakerScreenContent(title, description, events,onEventClicked, Modifier.padding(it))
    }
}

@Composable
fun SpeakerScreenContent(title: String, description: String, events: List<Event>, onEventClicked: (Event) -> Unit, modifier: Modifier) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        if (title.isNotBlank()) {
            Card(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(title, Modifier.padding(16.dp))
            }
        }
        if (description.isNotBlank()) {
            Paragraph(description, Modifier.padding(16.dp))
        } else {
            NoDetailsView()
        }

        if (events.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Events", textAlign = TextAlign.Center, modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            for (event in events) {
                EventRowView(
                    title = event.title,
                    time = TimeUtil.getTimeStamp(event.startTime, is24HourFormat = false),
                    location = event.location.name,
                    tags = event.types,
                    event.isBookmarked,
                    modifier = Modifier.clickable {
                        onEventClicked(event)
                    }
                ) {
                    // todo:
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpeakerScreenViewPreview(
    @PreviewParameter(SpeakerProvider::class) speaker: Speaker,
) {
    ScheduleTheme {
        SpeakerScreenView(speaker.name, speaker.title, speaker.description, emptyList(), {}) {

        }
    }
}