package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.navigation.ContentViewModel
import com.advice.ui.screens.ContentListScreen
import com.advice.ui.screens.ContentScreen


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
    val viewModel = navController.navGraphViewModel<ContentViewModel>()
    val flow = remember(conference, id) { viewModel.getContent(conference, id?.toLong()) }
    val content = flow.collectAsState(initial = null).value

    ContentScreen(
        event = content,
        onBookmark = {},
        onBackPressed = {
            navController.popBackStack()
        },
        onTagClicked = {

        },
        onUrlClicked = { url ->
            (context as MainActivity).openLink(url)
        },
        onSpeakerClicked = {
            navController.navigate("speaker/${it.id}/${it.name}")
        },
    )
}
