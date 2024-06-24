package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.NewsViewModel
import com.advice.ui.screens.NewsScreen

@Composable
internal fun News(navController: NavHostController, label: String?) {
    val viewModel = navController.navGraphViewModel<NewsViewModel>()

    val news = viewModel.getNews().collectAsState(initial = emptyList()).value

    NewsScreen(label = label, news = news) {
        navController.popBackStack()
    }
}
