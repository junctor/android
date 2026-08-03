package com.advice.feedback.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.advice.feedback.presentation.viewmodel.FeedbackViewModel
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.screens.ErrorScreen

/**
 * Route-level composable for the feedback form. Owns fetching and
 * loading/error/content state handling; the app module only supplies
 * the navigation back callback.
 */
@Composable
fun FeedbackRoute(
    viewModel: FeedbackViewModel,
    id: Long,
    content: Long?,
    onBackPressed: () -> Unit,
) {
    LaunchedEffect("$id/$content") {
        viewModel.fetchFeedbackForm(id)
    }
    when (val state = viewModel.state.collectAsState(initial = FeedbackState.Loading).value) {
        is FeedbackState.Error -> {
            ErrorScreen(
                message = state.exception.message ?: "Could not load feedback form",
                onBackPress = onBackPressed,
            )
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
                        onBackPressed()
                    }
                },
                onDiscardPressed = onBackPressed,
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
