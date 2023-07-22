package com.advice.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.advice.core.local.Conference
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun ConferenceDropdown(
    expanded: Boolean,
    conferences: List<Conference>,
    onConferenceClick: (Conference) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = expanded,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(Modifier.padding(bottom = 64.dp)) {
            for (conference in conferences) {
                ConferenceSelectorView(
                    conference
                ) {
                    onConferenceClick(conference)
                }
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun ConferenceDropdownPreview() {
    ScheduleTheme {
        ConferenceDropdown(true, listOf(Conference.Zero, Conference.Zero), {}, {})
    }
}
