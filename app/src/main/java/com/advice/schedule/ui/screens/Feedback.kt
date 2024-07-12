package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.advice.feedback.ui.screens.FeedbackState
import com.advice.schedule.presentation.viewmodel.FeedbackViewModel
import com.advice.ui.screens.ErrorScreen

@Composable
fun Feedback(navController: NavController, id: Long) {
    val viewModel = viewModel<FeedbackViewModel>().apply {
        fetchFeedbackForm(id)
    }
    val state = viewModel.feedbackForm.collectAsState(initial = FeedbackState.Loading).value
    if (state is FeedbackState.Success) {
        com.advice.feedback.ui.screens.Feedback(form = state.feedback,
            onValueChanged = { item, value ->
                viewModel.onValueChanged(item, value)
            },
            onBackPressed = {
                // todo: possibly show a warning dialog.
                navController.popBackStack()
            },
            onSubmitPressed = {
                // todo: submit to the backend.
                navController.popBackStack()
            })
    } else {
        ErrorScreen {

        }
    }
}
