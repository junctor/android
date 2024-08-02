package com.advice.schedule.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.advice.feedback.ui.components.DiscardPopup
import com.advice.feedback.ui.screens.FeedbackScreen
import com.advice.feedback.ui.screens.FeedbackState
import com.advice.schedule.navigation.onBackPressed
import com.advice.schedule.presentation.viewmodel.FeedbackViewModel
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.notifications.PopupContainer
import com.advice.ui.screens.ErrorScreen

@Composable
fun Feedback(navController: NavController, id: Long, content: Long) {
    val viewModel = viewModel<FeedbackViewModel>()
    LaunchedEffect("$id/$content") {
        viewModel.fetchFeedbackForm(id)
    }
    val state = viewModel.state.collectAsState(initial = FeedbackState.Loading).value
    when (state) {
        FeedbackState.Error ->
            ErrorScreen {
                navController.onBackPressed()
            }

        FeedbackState.Loading -> {
            ProgressSpinner()
        }

        is FeedbackState.Success -> {
            Box {
                FeedbackScreen(
                    form = state.feedback,
                    onValueChanged = { item, value ->
                        viewModel.onValueChanged(item, value)
                    },
                    onBackPressed = {
                        if (state.feedback.hasUserData) {
                            viewModel.onBackPressed()
                        } else {
                            navController.onBackPressed()
                        }
                    },
                    onSubmitPressed = {
                        viewModel.submitFeedback(content)
                        navController.onBackPressed()
                    }
                )

                if (state.showingDiscardPopup) {
                    PopupContainer(
                        onDismiss = { viewModel.onDiscardPopupCancelled() }
                    ) {
                        DiscardPopup(
                            onDiscard = {
                                navController.onBackPressed()
                            },
                            onCancel = {
                                viewModel.onDiscardPopupCancelled()
                            },
                        )
                    }
                }
            }
        }
    }
}
