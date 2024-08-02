package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.navigation.navigate
import com.advice.schedule.navigation.onBackPressed
import com.advice.schedule.navigation.toNavigation
import com.advice.schedule.presentation.viewmodel.MenuScreenState
import com.advice.schedule.presentation.viewmodel.MenuViewModel
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.screens.ErrorScreen


@Composable
internal fun Menu(
    navController: NavHostController,
    label: String?,
    id: Long?,
) {
    val viewModel = navController.navGraphViewModel<MenuViewModel>()
    val state = viewModel.state.collectAsState(initial = MenuScreenState.Loading).value

    when (state) {
        is MenuScreenState.Error -> {
            ErrorScreen {

            }
        }

        MenuScreenState.Loading -> {
            ProgressSpinner()
        }

        is MenuScreenState.Success -> {
            val menu = state.menu.find { it.id == id } ?: return
            MenuScreen(
                menu = menu,
                label = label ?: "",
                onNavigationClick = {
                    navController.navigate(it.toNavigation())
                },
                onBackPressed = {
                    navController.onBackPressed()
                }
            )
        }
    }
}
