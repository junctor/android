package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.ui.screens.SettingsScreenPreference
import com.advice.ui.screens.SettingsScreenViewState

sealed class Preferences(
    val key: String,
    val title: String,
    val summary: String? = null,
    val summaryOn: String? = null,
    val summaryOff: String? = null
) {
    data object ConferenceTimeZone : Preferences(
        key = "force_time_zone",
        title = "Events in {timezone}",
        summaryOn = "Using conference time zone",
        summaryOff = "Using device zone",
    )

    data object ShowSchedule : Preferences(
        key = "show_schedule_by_default",
        title = "Show schedule on launch",
        summaryOn = "App will open to schedule",
        summaryOff = "App will open to home",
    )

    data object FabShown : Preferences(
        key = "show_filter",
        title = "Show Schedule filter button",
        summaryOn = "Showing FAB",
        summaryOff = "Swipe over to filters",
    )

    data object AllowAnalytics : Preferences(
        key = "allow_analytics",
        title = "Send anonymous usage statistic",
    )

    data object EasterEggs : Preferences(
        key = "easter_eggs",
        title = "Easter eggs",
        summary = "???",
    )

    companion object {
        val entries = listOf(
            ConferenceTimeZone,
            ShowSchedule,
            FabShown,
            AllowAnalytics,
            EasterEggs,
        )
    }
}

class SettingsScreenViewStateProvider : PreviewParameterProvider<SettingsScreenViewState> {
    override val values: Sequence<SettingsScreenViewState>
        get() {
            val preferences = Preferences.entries.map {
                SettingsScreenPreference(
                    key = it.key,
                    title = it.title,
                    summary = it.summary,
                    summaryOn = it.summaryOn,
                    summaryOff = it.summaryOff,
                    isChecked = false,
                )
            }

            val state = SettingsScreenViewState(
                enableEasterEggs = true,
                preferences = preferences,
            )
            return listOf(state).asSequence()
        }


}
