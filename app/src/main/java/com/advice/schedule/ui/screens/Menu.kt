package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.MenuViewModel


@Composable
internal fun Menu(
    navController: NavHostController,
    label: String?,
    id: String?,
) {
    val viewModel = navController.navGraphViewModel<MenuViewModel>()
    val menus = viewModel.menu.collectAsState(initial = null).value ?: return
    val menu = menus.find { it.id.toString() == id } ?: return

    com.advice.documents.ui.screens.MenuScreen(menu = menu,
        label = label ?: "",
        onNavigationClick = {
            navController.navigate(it)
        },
        onBackPressed = {
            navController.popBackStack()
        })
}
