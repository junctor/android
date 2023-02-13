package com.advice.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenView() {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Settings") }, navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.ArrowBack, null)
            }
        })
    }) {
        SettingsScreenContent(Modifier.padding(it))
    }
}

@Composable
fun SettingsScreenContent(modifier: Modifier) {
    Column(modifier) {
        SwitchPreference("Events in (EUROPE/HELSINKI)", summaryOn = "Using conference's timezone", summaryOff = "Using device's timezone")
        SwitchPreference("Show filter button")
        SwitchPreference("Easter Eggs", summary = "???")
        SwitchPreference("Send anonymous usage statistics")
        DeveloperSection()
        TwitterBadge()
        VersionNumber()
    }
}

@Composable
private fun VersionNumber() {
    Text("Version 7.0.0 (1)", modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp), textAlign = TextAlign.Center)
}

@Composable
private fun DeveloperSection() {
    val text = buildAnnotatedString {
        append("Android client built with ♥ by ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("advice")
        }
    }

    Text(
        text, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), textAlign = TextAlign.Center
    )


}

@Composable
fun TwitterBadge() {
    Card(androidx.compose.ui.Modifier.padding(16.dp)) {
        Row(Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text("advice", fontWeight = FontWeight.Bold)
                Text("@_advice_dog")
            }
            OutlinedButton(onClick = { /*TODO*/ }) {
                Text("Follow")
            }
        }
    }
}

@Composable
fun SwitchPreference(title: String, summary: String? = null, summaryOn: String? = null, summaryOff: String? = null) {
    var checked by rememberSaveable {
        mutableStateOf(false)
    }

    Row(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(title, fontWeight = FontWeight.Bold)
            when {
                summary != null -> {
                    Text(summary)
                }

                summaryOn != null && summaryOff != null -> {
                    Text(if (checked) summaryOn else summaryOff)
                }
            }
        }
        Switch(checked = checked, onCheckedChange = {
            checked = it
        })
    }
}

@Preview(showBackground = true)
@Composable
fun SwitchPreferencePreview() {
    MaterialTheme {
        SwitchPreference("Events in (EUROPE/HELSINKI)", summaryOn = "Using conference's timezone", summaryOff = "Using device's timezone")
    }
}

@Preview(showBackground = true)
@Composable
fun SettingScreenViewPreview() {
    MaterialTheme {
        SettingScreenView()
    }
}