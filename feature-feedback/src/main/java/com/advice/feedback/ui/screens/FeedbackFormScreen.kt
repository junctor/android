package com.advice.feedback.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.feedback.FeedbackItem
import com.advice.feedback.ui.components.DiscardPopup
import com.advice.feedback.ui.preview.FeedbackFormProvider
import com.advice.ui.components.BackButton
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.notifications.PopupContainer
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FeedbackFormScreen(
    state: FeedbackState.Content,
    onBackPressed: () -> Unit,
    onDiscardPressed: () -> Unit,
    onCancelDiscardPressed: () -> Unit,
    onValueChanged: (FeedbackItem, String) -> Unit,
    onSubmitContent: () -> Unit,
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
                )
            } else {
                Column {
                    FeedbackContent(
                        form = state.feedback,
                        onValueChanged = onValueChanged,
                        onSubmitPressed = onSubmitContent,
                    )
                }

                if (state.isLoading) {
                    ProgressSpinner(
                        modifier =
                            Modifier.background(
                                MaterialTheme.colorScheme.background.copy(
                                    alpha = 0.5f,
                                ),
                            ),
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
}

@Composable
private fun CompletedScreen(errorMessage: String?) {
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
private fun LoadingFeedbackFormScreenPreview(
    @PreviewParameter(FeedbackFormProvider::class) feedback: FeedbackForm,
) {
    ScheduleTheme {
        FeedbackFormScreen(
            state =
                FeedbackState.Content(
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
private fun DiscardingFeedbackFormScreenPreview(
    @PreviewParameter(FeedbackFormProvider::class) feedback: FeedbackForm,
) {
    ScheduleTheme {
        FeedbackFormScreen(
            state =
                FeedbackState.Content(
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
private fun ErrorMessageFeedbackFormScreenPreview(
    @PreviewParameter(FeedbackFormProvider::class) feedback: FeedbackForm,
) {
    ScheduleTheme {
        FeedbackFormScreen(
            state =
                FeedbackState.Content(
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
private fun CompletedFeedbackFormScreenPreview(
    @PreviewParameter(FeedbackFormProvider::class) feedback: FeedbackForm,
) {
    ScheduleTheme {
        FeedbackFormScreen(
            state =
                FeedbackState.Content(
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
