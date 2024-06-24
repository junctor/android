package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.FAQViewModel
import com.advice.ui.screens.FAQScreen

@Composable
internal fun FAQ(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<FAQViewModel>()

    val state = viewModel.faqs.collectAsState(initial = null).value

    FAQScreen(faqs = state, onBackPress = {
        navController.popBackStack()
    })
}
