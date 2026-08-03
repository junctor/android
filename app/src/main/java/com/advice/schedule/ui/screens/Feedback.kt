package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.advice.feedback.presentation.viewmodel.FeedbackViewModel
import com.advice.feedback.ui.screens.FeedbackRoute
import com.advice.schedule.navigation.onBackPressed
import org.koin.androidx.compose.koinViewModel

@Composable
fun Feedback(
    navController: NavController,
    id: Long,
    content: Long?,
) {
    FeedbackRoute(
        viewModel = koinViewModel<FeedbackViewModel>(),
        id = id,
        content = content,
        onBackPressed = { navController.onBackPressed() },
    )
}
