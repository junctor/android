package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.core.ui.ScheduleFilter
import com.advice.locations.presentation.viewmodel.LocationsViewModel
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.ui.screens.ScheduleScreen
import com.advice.ui.states.ScheduleScreenState

@Composable
internal fun Locations(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<LocationsViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value ?: return
    com.advice.locations.ui.screens.LocationsScreen(containers = state.list, onToggleClicked = {
        viewModel.toggle(it)
    }, onScheduleClicked = {
        // todo: this should URL encode the title
        navController.navigate("location/${it.id}/${it.title.replace("/", "-")}")
    }, onBackPressed = {
        navController.popBackStack()
    })
}

@Composable
fun Location(navController: NavHostController, id: String?, label: String?) {
    val context = LocalContext.current
    val viewModel = viewModel<ScheduleViewModel>()
    val state = remember {
        viewModel.getState(ScheduleFilter.Location(id))
    }.collectAsState(initial = ScheduleScreenState.Loading).value

    ScheduleScreen(
        state = state,
        label = label,
        onBackPress = {
            navController.popBackStack()
        },
        onEventClick = {
            navController.navigate("event/${it.conference}/${it.id}")
        },
        onBookmarkClick = { event, isBookmarked ->
            viewModel.bookmark(event, isBookmarked)
            (context as MainActivity).requestNotificationPermission()
        },
    )
}

