package com.advice.schedule.ui.screens

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.core.local.Content
import com.advice.core.local.Session
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.navigate
import com.advice.schedule.presentation.viewmodel.ContentViewModel
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.screens.ContentListScreen
import com.advice.ui.screens.ContentScreen
import com.advice.ui.screens.ErrorScreen
import com.advice.ui.states.EventScreenState


@Composable
fun Contents(context: AppCompatActivity, navController: NavHostController, label: String?) {
    val viewModel = viewModel<ContentViewModel>(context)
    val state = viewModel.state.collectAsState(initial = null).value ?: return

    ContentListScreen(
        state = state,
        label = label,
        onMenuClick = {
            navController.popBackStack()
        },
        onContentClick = {
            navController.navigate(Navigation.Event(it.conference, it.id.toString()))
        }
    )
}

@Composable
fun Event(
    context: AppCompatActivity,
    navController: NavHostController,
    conference: String?,
    id: String?,
    session: String?
) {
    // todo: this should be another ViewModel
    val viewModel = viewModel<ScheduleViewModel>(context)
    LaunchedEffect("$conference/$id") {
        viewModel.getEvent(
            conference,
            id?.toLongOrNull(),
            session?.toLongOrNull()
        )
    }
    when (val state = viewModel.state.collectAsState(initial = EventScreenState.Loading).value) {
        is EventScreenState.Error -> {
            ErrorScreen {
                navController.popBackStack()
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

        is EventScreenState.Success -> {
            Content(
                context = context,
                content = state.content,
                session = state.session,
                navController = navController
            ) { content, session, isBookmarked ->
                viewModel.bookmark(state.content, session, isBookmarked)
            }
        }
    }
}

@Composable
private fun Content(
    context: Context,
    content: Content,
    session: Session?,
    navController: NavHostController,
    onBookmark: (Content, Session?, Boolean) -> Unit,
) {
    ContentScreen(
        content = content,
        session = session,
        onBookmark = { content, session, isBookmarked ->
            onBookmark(content, session, isBookmarked)
            (context as MainActivity).requestNotificationPermission()
        },
        onBackPressed = { navController.popBackStack() },
        onTagClicked = {
            navController.navigate(Navigation.Tag(it.id, it.label))
        },
        onLocationClicked = { location ->
            // todo: move this logic into the Navigation class.
            val label = location.shortName?.replace(
                "/", "\\"
            ) ?: ""
            navController.navigate(
                Navigation.Location(location.id, label)
            )
        },
        onSessionClicked = {
            navController.navigate(
                Navigation.Event(
                    content.conference,
                    content.id.toString(),
                    it.id.toString()
                )
            )
        },
        onUrlClicked = { url ->
            (context as MainActivity).openLink(url)
        },
        onSpeakerClicked = {
            navController.navigate(Navigation.Speaker(it.id, it.name))
        }
    )
}
