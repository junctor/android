package com.advice.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.shortstack.hackertracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreenView(
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
                Icon(painterResource(id = R.drawable.baseline_arrow_back_ios_new_24), null)
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
fun SettingsScreenContent(
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
        SwitchPreference("Show Schedule by default", isChecked = showSchedule) {
            onPreferenceChanged("show_schedule", it)
        }
        SwitchPreference("Show filter button", isChecked = showFilterButton) {
            onPreferenceChanged("show_filter", it)
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
fun TwitterBadge() {
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

@Composable
fun SwitchPreference(
    title: String,
    isChecked: Boolean,
    summary: String? = null,
    summaryOn: String? = null,
    summaryOff: String? = null,
    onPreferenceChanged: (Boolean) -> Unit
) {
    var checked by rememberSaveable {
        mutableStateOf(isChecked)
    }

    Row(
        Modifier
            .clickable {
                checked = !checked
                onPreferenceChanged(checked)
            }
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(16.dp)
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight(), verticalArrangement = Arrangement.Center
        ) {
            Text(title)
            when {
                summary != null -> {
                    Text(summary)
                }

                summaryOn != null && summaryOff != null -> {
                    Text(if (checked) summaryOn else summaryOff)
                }
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
                onPreferenceChanged(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.White
            )
        )
    }
}

@LightDarkPreview()
@Composable
fun SwitchPreferencePreview() {
    ScheduleTheme {
        Column {
            SwitchPreference(
                "Events in (EUROPE/HELSINKI)",
                true,
                summaryOn = "Using conference's timezone",
                summaryOff = "Using device's timezone"
            ) {

            }
            SwitchPreference("Show filter button", false) {}
        }
    }
}

@LightDarkPreview()
@Composable
fun SettingScreenViewDarkPreview() {
    ScheduleTheme {
        SettingScreenView(
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
