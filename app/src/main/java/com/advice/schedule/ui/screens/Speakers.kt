package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.SpeakerViewModel
import com.advice.schedule.presentation.viewmodel.SpeakersViewModel
import com.advice.ui.screens.SpeakerScreen
import com.advice.ui.screens.SpeakersScreen
import com.advice.ui.states.SpeakerState

@Composable
internal fun Speakers(navController: NavHostController, label: String) {
    val viewModel = navController.navGraphViewModel<SpeakersViewModel>()
    val state = viewModel.speakers.collectAsState(initial = null).value
    SpeakersScreen(
        label = label,
        speakers = state,
        onBackPress = { navController.popBackStack() },
        onSpeakerClick = { navController.navigate("speaker/${it.id}/${it.name}") },
    )
}

@Composable
fun Speaker(
    navController: NavHostController,
    id: String?,
    name: String?,
    onLinkClicked: (String) -> Unit,
) {
    val viewModel = navController.navGraphViewModel<SpeakerViewModel>()
    val speakerDetails by viewModel.speakerDetails.collectAsState(SpeakerState.Loading)

    LaunchedEffect(id) {
        viewModel.fetchSpeakerDetails(id)
    }

    SpeakerScreen(name = name ?: "", state = speakerDetails, onBackPress = {
        navController.popBackStack()
    }, onLinkClick = onLinkClicked, onEventClick = {
        navController.navigate("event/${it.conference}/${it.id}")
    })
}
