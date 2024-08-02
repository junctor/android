package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.navigation.onBackPressed
import com.advice.schedule.presentation.viewmodel.FAQScreenState
import com.advice.schedule.presentation.viewmodel.FAQViewModel
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.screens.ErrorScreen
import com.advice.ui.screens.FAQScreen

@Composable
internal fun FAQ(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<FAQViewModel>()

    val state = viewModel.state.collectAsState(initial = FAQScreenState.Loading).value

    when (state) {
        is FAQScreenState.Error -> {
            ErrorScreen(
                message = state.error.message ?: "An error occurred",
                onBackPress = {
                    navController.onBackPressed()
                }
            )
        }

        FAQScreenState.Loading -> {
            ProgressSpinner()
        }

        is FAQScreenState.Success -> {
            FAQScreen(faqs = state.faqs, onBackPress = {
                navController.onBackPressed()
            })
        }
    }
}
