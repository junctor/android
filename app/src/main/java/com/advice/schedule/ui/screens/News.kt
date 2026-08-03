package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.advice.core.local.FlowResult
import com.advice.news.presentation.viewmodel.NewsViewModel
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.navigation.onBackPressed
import com.advice.ui.screens.NewsScreen

@Composable
internal fun News(
    navController: NavHostController,
    label: String?,
) {
    val viewModel = navController.navGraphViewModel<NewsViewModel>()

    val newsResult = viewModel.getNews().collectAsState(initial = FlowResult.Loading).value
    val news =
        when (newsResult) {
            is FlowResult.Success -> newsResult.value
            else -> emptyList()
        }

    NewsScreen(label = label, news = news) {
        navController.onBackPressed()
    }
}
