package com.advice.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.R
import com.advice.ui.components.SwitchPreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    timeZone: String,
    version: String,
    useConferenceTimeZone: Boolean,
    showSchedule: Boolean,
    showFilterButton: Boolean,
    enableEasterEggs: Boolean,
    enableAnalytics: Boolean,
    showTwitterHandle: Boolean,
    onPreferenceChanged: (String, Boolean) -> Unit,
    onBackPressed: () -> Unit
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Settings") }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(painterResource(id = R.drawable.arrow_back), null)
            }
        })
    }) {
        SettingsScreenContent(
            timeZone,
            version,
            useConferenceTimeZone,
            showSchedule,
            showFilterButton,
            enableEasterEggs,
            enableAnalytics,
            showTwitterHandle,
            onPreferenceChanged,
            Modifier.padding(it)
        )
    }
}

@Composable
private fun SettingsScreenContent(
    timeZone: String,
    version: String,
    useConferenceTimeZone: Boolean,
    showSchedule: Boolean,
    showFilterButton: Boolean,
    enableEasterEggs: Boolean,
    enableAnalytics: Boolean,
    showTwitterHandle: Boolean,
    onPreferenceChanged: (String, Boolean) -> Unit,
    modifier: Modifier
) {
    Column(modifier) {
        SwitchPreference(
            "Events in ($timeZone)",
            isChecked = useConferenceTimeZone,
            summaryOn = "Using conference's timezone",
            summaryOff = "Using device's timezone"
        ) {
            onPreferenceChanged("force_time_zone", it)
        }
        SwitchPreference("Send anonymous usage statistics", isChecked = enableAnalytics) {
            onPreferenceChanged("allow_analytics", it)
        }
        SwitchPreference("Easter Eggs", summary = "???", isChecked = enableEasterEggs) {
            onPreferenceChanged("easter_eggs", it)
        }
        DeveloperSection()
        if (showTwitterHandle) {
            TwitterBadge()
        }
        VersionNumber(version)
    }
}

@Composable
private fun VersionNumber(version: String) {
    Text(
        "Version $version", modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp), textAlign = TextAlign.Center
    )
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
private fun TwitterBadge() {
    Card(Modifier.padding(16.dp)) {
        Row(Modifier.padding(16.dp)) {
            Image(
                painterResource(R.drawable.doggo),
                null,
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
            if (isSystemInDarkTheme()) {
                OutlinedButton(onClick = { /*TODO*/ }) {
                    Text("Follow")
                }
            } else {
                Button(onClick = { /*TODO*/ }) {
                    Text("Follow")
                }
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun SettingScreenViewDarkPreview() {
    ScheduleTheme {
        SettingScreen(
            timeZone = "Europe/Helsinki",
            version = "7.0.0 (1)",
            useConferenceTimeZone = true,
            showSchedule = false,
            showFilterButton = true,
            enableEasterEggs = false,
            enableAnalytics = true,
            showTwitterHandle = true, { _, _ ->

            }
        ) {

        }
    }
}
