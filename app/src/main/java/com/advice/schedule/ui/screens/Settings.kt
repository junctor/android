package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.SettingsViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.ui.screens.SettingScreen

@Composable
fun Settings(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = navController.navGraphViewModel<SettingsViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value ?: return

    SettingScreen(timeZone = state.timezone,
        version = state.version,
        useConferenceTimeZone = state.useConferenceTimeZone,
        showSchedule = state.showSchedule,
        showFilterButton = state.showFilterButton,
        enableEasterEggs = state.enableEasterEggs,
        enableAnalytics = state.enableAnalytics,
        showTwitterHandle = state.showTwitterHandle,
        onPreferenceChange = { id, value ->
            viewModel.onPreferenceChanged(id, value)
        },
        onThemeChange = {
            if (viewModel.onThemeChanged(it)) {
                // Recreate the Activity if the theme has changed
                (context as MainActivity).recreate()
            }
        },
        onVersionClick = {
            viewModel.onVersionClick()
            (context as MainActivity).openLink("https://www.youtube.com/watch?v=xvFZjo5PgG0")
        },
        onBackPress = { navController.popBackStack() })
}
