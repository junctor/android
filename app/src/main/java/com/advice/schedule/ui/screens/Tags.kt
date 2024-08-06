package com.advice.schedule.ui.screens

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.core.ui.ScheduleFilter
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.navigate
import com.advice.schedule.navigation.onBackPressed
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.ui.screens.ScheduleScreen
import com.advice.ui.states.ScheduleScreenState


@Composable
fun Tag(context: AppCompatActivity, navController: NavHostController, id: Long?, label: String?) {
    val viewModel = viewModel<ScheduleViewModel>(context)
    val state = remember {
        viewModel.getState(ScheduleFilter.Tag(id, label))
    }.collectAsState(initial = ScheduleScreenState.Loading).value

    ScheduleScreen(
        state = state,
        label = label,
        onBackPress = {
            navController.onBackPressed()
        },
        onEventClick = {
            navController.navigate(
                Navigation.Event(
                    it.conference,
                    it.content.id.toString(),
                    it.id.toString()
                )
            )
        },
        onBookmarkClick = { event, isBookmarked ->
            viewModel.bookmark(event, isBookmarked)
            (context as MainActivity).onBookmarkEvent()
        },
    )
}

@Composable
fun Tags(
    context: AppCompatActivity,
    navController: NavHostController,
    ids: List<Long>?,
    label: String?
) {
    val viewModel = viewModel<ScheduleViewModel>(context)
    val state = remember {
        viewModel.getState(ScheduleFilter.Tags(ids))
    }.collectAsState(initial = ScheduleScreenState.Loading).value

    ScheduleScreen(
        state = state,
        label = label,
        onBackPress = {
            navController.onBackPressed()
        },
        onEventClick = {
            navController.navigate(
                Navigation.Event(
                    it.conference,
                    it.content.id.toString(),
                    it.id.toString()
                )
            )
        },
        onBookmarkClick = { event, isBookmarked ->
            viewModel.bookmark(event, isBookmarked)
            (context as MainActivity).onBookmarkEvent()
        },
    )
}
