package com.advice.schedule.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.navigation.ContentViewModel
import com.advice.schedule.ui.navigation.Navigation
import com.advice.schedule.ui.navigation.navigate
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.screens.ContentListScreen
import com.advice.ui.screens.ContentScreen
import com.advice.ui.screens.ErrorScreen
import com.advice.ui.states.EventScreenState


@Composable
fun Contents(navController: NavHostController, label: String?) {
    val viewModel = navController.navGraphViewModel<ContentViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value ?: return

    ContentListScreen(
        state = state,
        label = label,
        onMenuClick = {
            navController.popBackStack()
        },
        onContentClick = {
            navController.navigate("content/${it.conference}/${it.id}")
        }
    )
}

@Composable
fun Content(navController: NavHostController, conference: String?, id: String?) {
    val context = LocalContext.current
    val viewModel = navController.navGraphViewModel<ScheduleViewModel>()
    val flow = remember(conference, id) { viewModel.getContent(conference, id?.toLong()) }
    val content = flow.collectAsState(initial = EventScreenState.Loading).value

    when (content) {
        is EventScreenState.Error -> {
            ErrorScreen {
                // todo: implement
            }
        }

        EventScreenState.Loading -> {
            Box(
                Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                ProgressSpinner()
            }
        }

        is EventScreenState.Success -> extracted(content, context, navController)
    }
}

@Composable
fun Event(navController: NavHostController, conference: String?, id: String?) {
    val context = LocalContext.current
    // todo: this should be another ViewModel
    val viewModel = navController.navGraphViewModel<ScheduleViewModel>()
    val flow = remember(conference, id) { viewModel.getEvent(conference, id?.toLong()) }
    val event = flow.collectAsState(initial = EventScreenState.Loading).value

    when (event) {
        is EventScreenState.Error -> {
            ErrorScreen {
                // todo: implement
            }
        }

        EventScreenState.Loading -> {
            Box(
                Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                ProgressSpinner()
            }
        }

        is EventScreenState.Success -> extracted(event, context, navController)
    }
}

@Composable
private fun extracted(
    event: EventScreenState.Success,
    context: Context,
    navController: NavHostController
) {
    ContentScreen(
        state = event,
        onBookmark = {
            //todo: viewModel.bookmark(event, it)
            (context as MainActivity).requestNotificationPermission()
        },
        onBackPressed = { navController.popBackStack() },
        onTagClicked = {
            navController.navigate(Navigation.Tag(it.id.toString(), it.label))
        },
        onLocationClicked = { location ->
            val label = location.shortName?.replace(
                "/", "\\"
            ) ?: ""
            navController.navigate(
                Navigation.Location(location.id.toString(), label)
            )
        },
        onSessionClicked = {
            navController.navigate(
                Navigation.Event(
                    event.content.conference,
                    event.content.id.toString(),
                    it.id.toString()
                )
            )
        },
        onUrlClicked = { url ->
            (context as MainActivity).openLink(url)
        },
        onSpeakerClicked = {
            navController.navigate(Navigation.Speaker(it.id.toString(), it.name))
        }
    )
}
