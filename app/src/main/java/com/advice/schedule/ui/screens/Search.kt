package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.navigateTo
import com.advice.schedule.navigation.onBackPressed
import com.advice.search.presentation.viewmodel.SearchViewModel
import com.advice.search.ui.screens.SearchScreen

@Composable
internal fun Search(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<SearchViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value
    val conference = viewModel.conference.collectAsState(initial = null).value
    SearchScreen(
        conference = conference,
        state = state,
        onQueryChanged = { viewModel.search(it) },
        onBackPressed = { navController.onBackPressed() },
        onTagClicked = { tag ->
            navController.navigateTo(Navigation.Tag(tag.id, tag.label))
        },
        onEventClicked = { event ->
            navController.navigateTo(
                Navigation.Event(
                    event.conference,
                    event.content.id.toString(),
                    event.id.toString(),
                ),
            )
        },
        onSpeakerClicked = { speaker ->
            navController.navigateTo(Navigation.Speaker(speaker.id, speaker.name))
        },
        onOrganizationClicked = { organization ->
            navController.navigateTo(Navigation.Organization(organization.id))
        },
    )
}
