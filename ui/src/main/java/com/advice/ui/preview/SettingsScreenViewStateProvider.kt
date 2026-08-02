package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.preferences.Preferences
import com.advice.ui.screens.SettingsScreenPreference
import com.advice.ui.screens.SettingsScreenViewState

class SettingsScreenViewStateProvider : PreviewParameterProvider<SettingsScreenViewState> {
    override val values: Sequence<SettingsScreenViewState>
        get() {
            val preferences =
                Preferences.entries.map {
                    SettingsScreenPreference(
                        key = it.key,
                        title = it.title,
                        summary = it.summary,
                        summaryOn = it.summaryOn,
                        summaryOff = it.summaryOff,
                        isChecked = false,
                    )
                }

            val state =
                SettingsScreenViewState(
                    enableEasterEggs = true,
                    preferences = preferences,
                )
            return listOf(state).asSequence()
        }
}
