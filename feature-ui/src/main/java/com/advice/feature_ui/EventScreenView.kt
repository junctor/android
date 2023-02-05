package com.advice.feature_ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.feature_ui.views.ActionView
import com.advice.feature_ui.views.CategoryView
import com.advice.feature_ui.views.SpeakerView

@Composable
fun EventScreenView() {
//    Scaffold(
//        topBar = { TopAppBar(title = { Text("Arcade Party") }) }, content = { EventScreenContent() }
//    )
}

// mock data
val actions = listOf("Discord.com", "Discord.com")
val speakers = listOf("John McAfee")

@Composable
fun EventScreenContent() {
    Column(Modifier.padding(16.dp)) {
        HeaderSection()
        Text(
            "The Bug Hunter's Methodology is an ongoing yearly installment on the newest tools and tech. for bug hunters and red teamers.",
            modifier = Modifier.padding(vertical = 16.dp)
        )
        Card {
            LazyColumn {
                items(actions) {
                    ActionView()
                }
            }
        }
        if (speakers.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text("Speakers", style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            LazyColumn {
                items(speakers) {
                    SpeakerView(it)
                }
            }
        }

    }
}

@Composable
fun HeaderSection() {
    Column {
        Text("Arcade Party", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.padding(16.dp)) {
            CategoryView()
        }
        DetailsCard("August 16th, 9:30am - 10:30am")
        DetailsCard("Tap the location to show all events from this location", isDismissible = true)
        DetailsCard("Caesars Forum - Forum 104-105")
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
        EventScreenContent()
    }
}