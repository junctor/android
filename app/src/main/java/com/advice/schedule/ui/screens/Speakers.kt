package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.core.network.report.ReportObjectType
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.navigateTo
import com.advice.schedule.navigation.onBackPressed
import com.advice.schedule.presentation.viewmodel.ReportViewModel
import com.advice.schedule.presentation.viewmodel.SpeakerViewModel
import com.advice.schedule.presentation.viewmodel.SpeakersViewModel
import com.advice.ui.screens.SpeakerScreen
import com.advice.ui.screens.SpeakersScreen
import com.advice.ui.states.SpeakerState

@Composable
internal fun Speakers(
    navController: NavHostController,
    label: String,
) {
    val viewModel = navController.navGraphViewModel<SpeakersViewModel>()
    val state = viewModel.speakers.collectAsState(initial = null).value
    SpeakersScreen(
        label = label,
        speakers = state,
        onBackPress = {
            navController.onBackPressed()
        },
        onSpeakerClick = {
            navController.navigateTo(Navigation.Speaker(it.id, it.name))
        },
    )
}

@Composable
fun Speaker(
    navController: NavHostController,
    id: Long?,
    name: String?,
    onLinkClicked: (String) -> Unit,
) {
    val viewModel = navController.navGraphViewModel<SpeakerViewModel>()
    val reportViewModel = viewModel<ReportViewModel>()
    val speakerDetails by viewModel.speakerDetails.collectAsState(SpeakerState.Loading)

    LaunchedEffect(id) {
        viewModel.fetchSpeakerDetails(id)
    }

    SpeakerScreen(
        name = name ?: "",
        state = speakerDetails,
        onBackPress = {
            navController.onBackPressed()
        },
        onLinkClick = onLinkClicked,
        onEventClick = {
            navController.navigateTo(
                Navigation.Event(
                    it.conference,
                    it.content.id.toString(),
                    it.id.toString(),
                ),
            )
        },
        onReport = { message ->
            val speakerId =
                (speakerDetails as? SpeakerState.Success)?.speaker?.id ?: id ?: return@SpeakerScreen
            reportViewModel.submit(
                message = message,
                objectType = ReportObjectType.PERSON,
                objectId = speakerId,
            )
        },
    )
}
