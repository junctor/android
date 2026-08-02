package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.advice.feedback.presentation.viewmodel.FeedbackViewModel
import com.advice.feedback.ui.screens.FeedbackFormScreen
import com.advice.feedback.ui.screens.FeedbackState
import com.advice.schedule.navigation.onBackPressed
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.screens.ErrorScreen

@Composable
fun Feedback(
    navController: NavController,
    id: Long,
    content: Long?,
) {
    val viewModel = viewModel<FeedbackViewModel>()
    LaunchedEffect("$id/$content") {
        viewModel.fetchFeedbackForm(id)
    }
    when (val state = viewModel.state.collectAsState(initial = FeedbackState.Loading).value) {
        is FeedbackState.Error -> {
            ErrorScreen(
                message = state.exception.message ?: "Could not load feedback form",
            ) {
                navController.onBackPressed()
            }
        }

        FeedbackState.Loading -> {
            ProgressSpinner()
        }

        is FeedbackState.Content -> {
            FeedbackFormScreen(
                state = state,
                onBackPressed = {
                    if (!state.isComplete && state.feedback.hasUserData) {
                        viewModel.onBackPressed()
                    } else {
                        navController.onBackPressed()
                    }
                },
                onDiscardPressed = {
                    navController.onBackPressed()
                },
                onCancelDiscardPressed = {
                    viewModel.onDiscardPopupCancelled()
                },
                onValueChanged = { item, value ->
                    viewModel.onValueChanged(item, value)
                },
                onSubmitContent = {
                    viewModel.submitFeedback(content)
                },
            )
        }
    }
}
