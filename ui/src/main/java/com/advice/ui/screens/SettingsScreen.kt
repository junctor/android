package com.advice.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.ScheduleDayFormat
import com.advice.core.utils.TimeUtil
import com.advice.ui.R
import com.advice.ui.components.BackButton
import com.advice.ui.components.ButtonPreference
import com.advice.ui.components.PreferenceOption
import com.advice.ui.components.SectionHeader
import com.advice.ui.components.SwitchPreference
import com.advice.ui.preview.Preferences
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.preview.SettingsScreenViewStateProvider
import com.advice.ui.theme.ScheduleTheme
import java.time.Instant

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
    val scheduleDayFormat: String = ScheduleDayFormat.MonthDay.id,
    val preferences: List<SettingsScreenPreference> = emptyList(),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    state: SettingsScreenViewState,
    onPreferenceChange: (String, Boolean) -> Unit,
    onThemeChange: (String) -> Unit,
    onScheduleDayFormatChange: (String) -> Unit,
    onVersionClick: (Int) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
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
            onScheduleDayFormatChange,
            onVersionClick,
            onPrivacyPolicyClick,
            Modifier.padding(it),
        )
    }
}

@Composable
private fun SettingsScreenContent(
    state: SettingsScreenViewState,
    onPreferenceChange: (String, Boolean) -> Unit,
    onThemeChange: (String) -> Unit,
    onScheduleDayFormatChange: (String) -> Unit,
    onVersionClick: (Int) -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val enableEasterEggs by remember { mutableStateOf(state.enableEasterEggs) }
    val now = remember { Instant.now() }
    val dayFormatOptions =
        remember(now) {
            ScheduleDayFormat.entries.map { format ->
                PreferenceOption(
                    title = TimeUtil.formatScheduleDay(now, format),
                    value = format.id,
                )
            }
        }
    val selectedDayFormat = ScheduleDayFormat.fromId(state.scheduleDayFormat)
    val dayFormatSummary = TimeUtil.formatScheduleDay(now, selectedDayFormat)
    val preferencesByKey =
        remember(state.preferences) {
            state.preferences.associateBy { it.key }
        }

    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 16.dp),
    ) {
        SectionHeader(stringResource(R.string.settings_section_schedule))
        ButtonPreference(
            title = "Schedule day format",
            options = dayFormatOptions,
            summary = dayFormatSummary,
            onPreferenceChange = onScheduleDayFormatChange,
        )
        PreferenceSwitch(
            preference = preferencesByKey[Preferences.ConferenceTimeZone.key],
            onPreferenceChange = onPreferenceChange,
        )
        PreferenceSwitch(
            preference = preferencesByKey[Preferences.ShowSchedule.key],
            onPreferenceChange = onPreferenceChange,
        )
        PreferenceSwitch(
            preference = preferencesByKey[Preferences.FabShown.key],
            onPreferenceChange = onPreferenceChange,
        )

        SectionHeader(stringResource(R.string.settings_section_privacy))
        PreferenceSwitch(
            preference = preferencesByKey[Preferences.AllowAnalytics.key],
            onPreferenceChange = onPreferenceChange,
        )
        PreferenceSwitch(
            preference = preferencesByKey[Preferences.AllowCrashlytics.key],
            onPreferenceChange = onPreferenceChange,
        )
        Text(
            text = stringResource(R.string.privacy_policy_title),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onPrivacyPolicyClick)
                    .padding(16.dp),
        )

        SectionHeader(stringResource(R.string.settings_section_appearance))
        ButtonPreference(
            title = "Choose theme",
            options =
                listOf(
                    PreferenceOption("Light", "light"),
                    PreferenceOption("Dark", "dark"),
                    PreferenceOption("System default", "system"),
                ),
            onPreferenceChange = onThemeChange,
        )
        PreferenceSwitch(
            preference = preferencesByKey[Preferences.GlitchAnimation.key],
            onPreferenceChange = onPreferenceChange,
        )

        SectionHeader(stringResource(R.string.settings_section_extras))
        PreferenceSwitch(
            preference = preferencesByKey[Preferences.EasterEggs.key],
            onPreferenceChange = onPreferenceChange,
        )

        DeveloperSection()
        VersionNumber(state.version, enableEasterEggs, onVersionClick)
    }
}

@Composable
private fun PreferenceSwitch(
    preference: SettingsScreenPreference?,
    onPreferenceChange: (String, Boolean) -> Unit,
) {
    if (preference == null) return

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
                }.padding(16.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun DeveloperSection() {
    val text =
        buildAnnotatedString {
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
            onScheduleDayFormatChange = {},
            onVersionClick = {},
            onPrivacyPolicyClick = {},
            onBackPress = {},
        )
    }
}
