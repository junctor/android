package com.advice.ui.views.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.advice.core.local.Conference
import com.advice.core.ui.HomeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConferenceSelector(state: HomeState.Loaded?, onConferenceClick: (Conference) -> Unit) {
    if (state == null) {
        CenterAlignedTopAppBar(title = { Text("Home") })
        return
    }

    var expanded by rememberSaveable {
        mutableStateOf(value = false)
    }

    Box(modifier = Modifier
        .clickable {
            expanded = !expanded
        }
        .fillMaxWidth()) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(state.conference.name)
            Icon(Icons.Default.ArrowDropDown, null)
        }
    }
    ConferenceDropdown(
        expanded = expanded,
        conferences = state.conferences,
        onConferenceClick = {
            onConferenceClick(it)
            expanded = false
        },
        onDismiss = {
            expanded = false
        })
}
