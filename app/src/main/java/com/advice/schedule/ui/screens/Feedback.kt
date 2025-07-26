package com.advice.schedule.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.feedback.FeedbackItem
import com.advice.feedback.ui.components.DiscardPopup
import com.advice.feedback.ui.preview.FeedbackFormProvider
import com.advice.feedback.ui.screens.FeedbackContent
import com.advice.feedback.ui.screens.FeedbackState
import com.advice.schedule.navigation.onBackPressed
import com.advice.schedule.presentation.viewmodel.FeedbackViewModel
import com.advice.ui.components.BackButton
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.notifications.PopupContainer
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.screens.ErrorScreen
import com.advice.ui.theme.ScheduleTheme

@Composable
fun Feedback(navController: NavController, id: Long, content: Long) {
    val viewModel = viewModel<FeedbackViewModel>()
    LaunchedEffect("$id/$content") {
        viewModel.fetchFeedbackForm(id)
    }
    val state = viewModel.state.collectAsState(initial = FeedbackState.Loading).value
    when (state) {
        is FeedbackState.Error -> {
            ErrorScreen(
                message = state.exception.message ?: "Could not load feedback form"
            ) {
                navController.onBackPressed()
            }
        }

        FeedbackState.Loading -> {
            ProgressSpinner()
        }

        is FeedbackState.Content -> {
            FeedbackScreen(
                state = state,
                onBackPressed = {
                    if (state.feedback.hasUserData) {
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun FeedbackScreen(
    state: FeedbackState.Content,
    onBackPressed: () -> Unit,
    onDiscardPressed: () -> Unit,
    onCancelDiscardPressed: () -> Unit,
    onValueChanged: (FeedbackItem, String) -> Unit,
    onSubmitContent: () -> Unit
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(state.feedback.title)
            },
            navigationIcon = {
                BackButton(onClick = onBackPressed)
            },
        )
    }) {
        Box(Modifier.padding(it)) {
            if (state.isComplete) {
                CompletedScreen(
                    errorMessage = state.errorMessage,
                ) {
                    onBackPressed()
                }
                return@Scaffold
            }
            Column {
                FeedbackContent(
                    form = state.feedback,
                    onValueChanged = onValueChanged,
                    onSubmitPressed = onSubmitContent,
                )
            }

            if (state.isLoading) {
                ProgressSpinner(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                )
            }

            if (state.showingDiscardPopup) {
                PopupContainer(
                    onDismiss = onCancelDiscardPressed,
                ) {
                    DiscardPopup(
                        onDiscard = onDiscardPressed,
                        onCancel = onCancelDiscardPressed,
                    )
                }
            }
        }
    }
}

@Composable
private fun CompletedScreen(errorMessage: String?, onCompletePressed: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        if (errorMessage != null) {
            Text(
                text = "We encountered an error while submitting your feedback.",
                fontSize = 32.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 38.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "We will automatically attempt to resubmit your feedback at a later time.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                lineHeight = 32.sp,
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = errorMessage,
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                lineHeight = 24.sp,
            )
        } else {
            Text(
                text = "Thank you for your feedback!",
                fontSize = 48.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 48.sp,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "You can now power off your computer.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Light,
                lineHeight = 28.sp,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LoadingFeedbackScreenPreview(
    @PreviewParameter(FeedbackFormProvider::class) feedback: FeedbackForm,
) {
    ScheduleTheme {
        FeedbackScreen(
            state = FeedbackState.Content(
                feedback = feedback,
                isLoading = true,
            ),
            onBackPressed = { },
            onDiscardPressed = { },
            onCancelDiscardPressed = {},
            onValueChanged = { _, _ -> },
            onSubmitContent = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun DiscardingFeedbackScreenPreview(
    @PreviewParameter(FeedbackFormProvider::class) feedback: FeedbackForm,
) {
    ScheduleTheme {
        FeedbackScreen(
            state = FeedbackState.Content(
                feedback = feedback,
                showingDiscardPopup = true,
            ),
            onBackPressed = { },
            onDiscardPressed = { },
            onCancelDiscardPressed = {},
            onValueChanged = { _, _ -> },
            onSubmitContent = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun ErrorMessageFeedbackScreenPreview(
    @PreviewParameter(FeedbackFormProvider::class) feedback: FeedbackForm,
) {
    ScheduleTheme {
        FeedbackScreen(
            state = FeedbackState.Content(
                feedback = feedback,
                isComplete = true,
                errorMessage = "404: url is not defined",
            ),
            onBackPressed = { },
            onDiscardPressed = { },
            onCancelDiscardPressed = {},
            onValueChanged = { _, _ -> },
            onSubmitContent = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun CompletedFeedbackScreenPreview(
    @PreviewParameter(FeedbackFormProvider::class) feedback: FeedbackForm,
) {
    ScheduleTheme {
        FeedbackScreen(
            state = FeedbackState.Content(
                feedback = feedback,
                isComplete = true,
            ),
            onBackPressed = { },
            onDiscardPressed = { },
            onCancelDiscardPressed = {},
            onValueChanged = { _, _ -> },
            onSubmitContent = {},
        )
    }
}
