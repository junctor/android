package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.ui.activity.MainActivity

@Composable
fun Event(navController: NavHostController, conference: String?, id: String?) {
    val context = LocalContext.current
    // todo: this should be another ViewModel
    val viewModel = navController.navGraphViewModel<ScheduleViewModel>()
    val flow = remember(conference, id) { viewModel.getEvent(conference, id?.toLong()) }
    val event = flow.collectAsState(initial = null).value
    com.advice.ui.screens.EventScreen(event = event, onBookmark = {
        if (event != null) {
            viewModel.bookmark(event, it)
            (context as MainActivity).requestNotificationPermission()
        }
    }, onBackPressed = { navController.popBackStack() }, onTagClicked = {
        navController.navigate("tag/${it.id}/${it.label}")
    }, onLocationClicked = { location ->
        navController.navigate(
            "location/${location.id}/${
                location.shortName?.replace(
                    "/", "\\"
                )
            }"
        )
    }, onUrlClicked = { url ->
        (context as MainActivity).openLink(url)
    }, onSpeakerClicked = { navController.navigate("speaker/${it.id}/${it.name}") })
}
