package com.advice.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Event
import com.advice.core.local.Speaker
import com.advice.ui.preview.SpeakerProvider
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.components.Paragraph
import com.advice.ui.R
import com.advice.ui.components.ClickableUrl
import com.advice.ui.components.EventRow
import com.advice.ui.components.NoDetailsView
import com.advice.ui.components.ProgressSpinner

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
        CenterAlignedTopAppBar(title = {
            Column {
                Text(name)
                if (state is SpeakerState.Success) {
                    val pronouns = state.speaker.pronouns
                    if (pronouns != null) {
                        Text(
                            pronouns,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

        }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(painterResource(id = R.drawable.arrow_back), null)
            }
        })
    }) {
        Box(Modifier.padding(it)) {
            when (state) {
                SpeakerState.Error -> {}
                SpeakerState.Loading -> {
                    ProgressSpinner()
                }

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
    val context = LocalContext.current

    Column(modifier.verticalScroll(rememberScrollState())) {
        if (speaker.affiliations.isNotEmpty()) {
            speaker.affiliations.forEach {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    shape = RoundedCornerShape(0.dp),
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
        } else {
            NoDetailsView(text = "This speaker likes to keep a low profile.")
        }
        if (speaker.links.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            speaker.links.forEach {
                ClickableUrl(label = it.title, url = it.url, onClick = {
                    onLinkClicked(it.url)
                }, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
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
                EventRow(
                    event = event,
                    onEventPressed = {
                        onEventClicked(event)
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpeakerScreenPreview(
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