package com.advice.schedule.ui.screens

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.navigation.onBackPressed
import com.advice.schedule.presentation.viewmodel.SettingsViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.ui.screens.SettingScreen

@Composable
fun Settings(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = navController.navGraphViewModel<SettingsViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value ?: return

    var toast by remember { mutableStateOf<Toast?>(null) }

    SettingScreen(
        state = state,
        onPreferenceChange = { id, value ->
            viewModel.onPreferenceChanged(id, value)
        },
        onThemeChange = {
            if (viewModel.onThemeChanged(it)) {
                // Recreate the Activity if the theme has changed
                (context as MainActivity).recreate()
            }
        },
        onVersionClick = { index ->
            if (index == 0) {
                toast?.cancel()
                viewModel.onVersionClick()
                (context as MainActivity).openLink("https://www.youtube.com/watch?v=xvFZjo5PgG0")
            } else {
                toast?.cancel()
                toast = Toast.makeText(context, "$index", Toast.LENGTH_SHORT)
                toast?.show()
            }
        },
        onBackPress = { navController.onBackPressed() })
}
