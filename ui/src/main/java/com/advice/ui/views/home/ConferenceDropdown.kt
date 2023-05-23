package com.advice.ui.views.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.advice.core.local.Conference
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.views.ConferenceSelectorView

@Composable
fun ConferenceDropdown(
    expanded: Boolean,
    conferences: List<Conference>,
    onConferenceClick: (Conference) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.background(Color.Green),
        offset = DpOffset(32.dp, 140.dp)
    ) {
        Column(Modifier.background(Color.Black)) {
            for (conference in conferences) {
                ConferenceSelectorView(
                    conference.name,
                    conference.startDate,
                    conference.endDate,
                    conference.hasFinished
                ) {
                    onConferenceClick(conference)
                }
            }
        }
    }
}

@LightDarkPreview
@Composable
fun ConferenceDropdownPreview() {
    ScheduleTheme {
        ConferenceDropdown(true, listOf(Conference.Zero, Conference.Zero), {}, {})
    }
}
