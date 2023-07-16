package com.advice.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Event
import com.advice.core.local.Speaker
import com.advice.core.utils.TimeUtil
import com.advice.ui.preview.SpeakerProvider
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.components.EventRowView
import com.advice.ui.components.NoDetailsView
import com.advice.ui.components.Paragraph
import com.advice.ui.R

sealed class SpeakerState {
    object Loading : SpeakerState()
    data class Success(val speaker: Speaker, val events: List<Event>) : SpeakerState()
    object Error : SpeakerState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakerScreen(
    name: String,
    state: SpeakerState,
    onBackPressed: () -> Unit,
    onLinkClicked: (String) -> Unit,
    onEventClicked: (Event) -> Unit
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text(name) }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(painterResource(id = R.drawable.baseline_arrow_back_ios_new_24), null)
            }
        })
    }) {
        Box(Modifier.padding(it)) {
            when (state) {
                SpeakerState.Error -> {}
                SpeakerState.Loading -> {}
                is SpeakerState.Success -> {
                    SpeakerScreenContent(
                        state.speaker,
                        state.events,
                        onLinkClicked,
                        onEventClicked,
                    )
                }
            }
        }
    }
}

@Composable
fun SpeakerScreenContent(
    speaker: Speaker,
    events: List<Event>,
    onLinkClicked: (String) -> Unit,
    onEventClicked: (Event) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        if (speaker.title.isNotBlank()) {
            Surface(
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(speaker.title, Modifier.padding(16.dp))
            }
        }
        if (speaker.affiliations.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Affiliations", textAlign = TextAlign.Center, modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            speaker.affiliations.forEach {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(it.organization)
                        Text(it.title)
                    }
                }
            }
        }

        if (speaker.description.isNotBlank()) {
            Paragraph(speaker.description, Modifier.padding(16.dp))
        }

        if (speaker.links.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Links", textAlign = TextAlign.Center, modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
            speaker.links.forEach {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column(Modifier
                        .clickable {
                            onLinkClicked(it.url)
                        }
                        .padding(16.dp)) {
                        Text(it.title)
                        Text(it.url)
                    }
                }
            }
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
        val state = SpeakerState.Success(speaker, emptyList())
        SpeakerScreen(
            name = speaker.name,
            state = state,
            onBackPressed = {},
            onEventClicked = {

            },
            onLinkClicked = {

            },
        )
    }
}