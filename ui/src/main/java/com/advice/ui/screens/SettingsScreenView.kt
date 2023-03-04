package com.advice.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
fun SettingScreenView(timeZone: String, version: String, code: Int, onBackPressed: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Settings") }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, null)
            }
        })
    }) {
        SettingsScreenContent(timeZone, version, code, Modifier.padding(it))
    }
}

@Composable
fun SettingsScreenContent(timeZone: String, version: String, code: Int, modifier: Modifier) {
    Column(modifier) {
        SwitchPreference("Events in ($timeZone)", isChecked = true, summaryOn = "Using conference's timezone", summaryOff = "Using device's timezone")
        SwitchPreference("Show filter button", isChecked = true)
        SwitchPreference("Easter Eggs", summary = "???", isChecked = false)
        SwitchPreference("Send anonymous usage statistics", isChecked = true)
        DeveloperSection()
        TwitterBadge()
        VersionNumber(version, code)
    }
}

@Composable
private fun VersionNumber(version: String, code: Int) {
    Text(
        "Version $version ($code)", modifier = Modifier
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
fun SwitchPreference(title: String, isChecked: Boolean, summary: String? = null, summaryOn: String? = null, summaryOff: String? = null) {
    var checked by rememberSaveable {
        mutableStateOf(isChecked)
    }

    Row(
        Modifier
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
        Switch(checked = checked, onCheckedChange = {
            checked = it
        }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, uncheckedThumbColor = Color.White))
    }
}

@LightDarkPreview()
@Composable
fun SwitchPreferencePreview() {
    ScheduleTheme {
        Column {
            SwitchPreference("Events in (EUROPE/HELSINKI)", true, summaryOn = "Using conference's timezone", summaryOff = "Using device's timezone")
            SwitchPreference("Show filter button", false)
        }
    }
}

@LightDarkPreview()
@Composable
fun SettingScreenViewDarkPreview() {
    ScheduleTheme {
        SettingScreenView("Europe/Helsinki", "7.0.0", 1) {

        }
    }
}
