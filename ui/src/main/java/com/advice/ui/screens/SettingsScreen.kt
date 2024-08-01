package com.advice.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.ui.R
import com.advice.ui.components.BackButton
import com.advice.ui.components.ButtonPreference
import com.advice.ui.components.SwitchPreference
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.preview.SettingsScreenViewStateProvider
import com.advice.ui.theme.ScheduleTheme

data class SettingsScreenPreference(
    val key: String,
    val title: String,
    val summary: String? = null,
    val summaryOn: String? = null,
    val summaryOff: String? = null,
    val isChecked: Boolean,
)

data class SettingsScreenViewState(
    val timeZone: String = "American/Los_Angeles",
    val version: String = "1.0.0",
    val enableEasterEggs: Boolean = false,
    val showTwitterHandle: Boolean = false,
    val preferences: List<SettingsScreenPreference> = emptyList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    state: SettingsScreenViewState,
    onPreferenceChange: (String, Boolean) -> Unit,
    onThemeChange: (String) -> Unit,
    onVersionClick: (Int) -> Unit,
    onBackPress: () -> Unit,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Settings") }, navigationIcon = {
            BackButton(onBackPress)
        })
    }) {
        SettingsScreenContent(
            state,
            onPreferenceChange,
            onThemeChange,
            onVersionClick,
            Modifier.padding(it),
        )
    }
}

@Composable
private fun SettingsScreenContent(
    state: SettingsScreenViewState,
    onPreferenceChange: (String, Boolean) -> Unit,
    onThemeChange: (String) -> Unit,
    onVersionClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val enableEasterEggs by remember { mutableStateOf(state.enableEasterEggs) }

    Column(modifier) {
        ButtonPreference(onPreferenceChange = {
            onThemeChange(it)
        })
        for (preference in state.preferences) {
            SwitchPreference(
                title = preference.title,
                summary = preference.summary,
                summaryOn = preference.summaryOn,
                summaryOff = preference.summaryOff,
                isChecked = preference.isChecked,
            ) {
                onPreferenceChange(preference.key, it)
            }
        }
        DeveloperSection()
        if (state.showTwitterHandle) {
            TwitterBadge()
        }
        VersionNumber(state.version, enableEasterEggs, onVersionClick)
    }
}

@Composable
private fun VersionNumber(
    version: String,
    enableEasterEggs: Boolean,
    onVersionClick: (Int) -> Unit = {},
) {
    var countdown by remember { mutableIntStateOf(10) }

    Text(
        "Version $version",
        modifier =
        Modifier
            .fillMaxWidth()
            .clickable(enabled = enableEasterEggs) {
                countdown--
                onVersionClick(countdown)
            }
            .padding(16.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun DeveloperSection() {
    val text = buildAnnotatedString {
        append("Android client is built with ♥ by ")
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
            append("advice")
        }
    }

    Text(
        text,
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun TwitterBadge() {
    Card(Modifier.padding(16.dp)) {
        Row(Modifier.padding(16.dp)) {
            Image(
                painterResource(R.drawable.doggo),
                null,
                modifier =
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White),
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

@PreviewLightDark
@Composable
private fun SettingScreenViewDarkPreview(
    @PreviewParameter(SettingsScreenViewStateProvider::class) state: SettingsScreenViewState,
) {
    ScheduleTheme {
        SettingScreen(
            state = state,
            onPreferenceChange = { _, _ -> },
            onThemeChange = {},
            onVersionClick = {},
            onBackPress = {},
        )
    }
}
