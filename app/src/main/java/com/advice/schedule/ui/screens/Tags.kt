package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.core.ui.ScheduleFilter
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.navigation.Navigation
import com.advice.schedule.ui.navigation.navigate
import com.advice.ui.screens.ScheduleScreen
import com.advice.ui.states.ScheduleScreenState


@Composable
fun Tag(navController: NavHostController, id: String?, label: String?) {
    val context = LocalContext.current
    val viewModel = viewModel<ScheduleViewModel>()
    val state = remember {
        viewModel.getState(ScheduleFilter.Tag(id))
    }.collectAsState(initial = ScheduleScreenState.Loading).value

    ScheduleScreen(
        state = state,
        label = label,
        onBackPress = {
            navController.popBackStack()
        },
        onEventClick = {
            navController.navigate(Navigation.Event(it.conference, it.content.id.toString(), it.id.toString()))
        },
        onBookmarkClick = { event, isBookmarked ->
            viewModel.bookmark(event, isBookmarked)
            (context as MainActivity).requestNotificationPermission()
        },
    )
}

@Composable
fun Tags(navController: NavHostController, id: String?, label: String?) {
    val context = LocalContext.current
    val viewModel = viewModel<ScheduleViewModel>()
    val state = remember {
        viewModel.getState(ScheduleFilter.Tags(id!!.split(",")))
    }.collectAsState(initial = ScheduleScreenState.Loading).value

    ScheduleScreen(
        state = state,
        label = label,
        onBackPress = {
            navController.popBackStack()
        },
        onEventClick = {
            navController.navigate(Navigation.Event(it.conference, it.content.id.toString(), it.id.toString()))
        },
        onBookmarkClick = { event, isBookmarked ->
            viewModel.bookmark(event, isBookmarked)
            (context as MainActivity).requestNotificationPermission()
        },
    )
}
