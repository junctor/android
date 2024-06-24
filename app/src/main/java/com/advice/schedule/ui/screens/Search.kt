package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.SearchViewModel

@Composable
internal fun Search(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<SearchViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value
    val conference = viewModel.conference.collectAsState(initial = null).value
    SearchScreen(navController, conference, state, onQueryChanged = {
        viewModel.search(it)
    })
}
