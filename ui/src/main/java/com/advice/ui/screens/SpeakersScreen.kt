package com.advice.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.advice.core.local.Speaker
import com.advice.ui.R
import com.advice.ui.components.EmptyMessage
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.Speaker
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.preview.SpeakerProvider
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpeakersScreen(
    speakers: List<Speaker>?,
    onBackPressed: () -> Unit,
    onSpeakerClicked: (Speaker) -> Unit
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Speakers") }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(painterResource(id = R.drawable.arrow_back), null)
            }
        })
    }) {
        Box(Modifier.padding(it)) {
            when {
                speakers == null -> {
                    ProgressSpinner()
                }

                speakers.isEmpty() -> {
                    EmptyMessage("Speakers not found")
                }

                else -> {
                    SpeakersScreenContent(speakers, onSpeakerClicked)
                }
            }
        }
    }
}

@Composable
private fun SpeakersScreenContent(
    speakers: List<Speaker>?,
    onSpeakerClicked: (Speaker) -> Unit
) {
    LazyColumn() {
        if (speakers != null) {
            items(speakers) {
                Speaker(
                    name = it.name,
                    title = it.title,
                    onSpeakerClicked = { onSpeakerClicked(it) }
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun SpeakersScreenViewPreview(@PreviewParameter(SpeakerProvider::class) speaker: Speaker) {
    ScheduleTheme {
        SpeakersScreen(listOf(speaker), {}, {})
    }
}
