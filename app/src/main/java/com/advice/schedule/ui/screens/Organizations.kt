package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.OrganizationsViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.navigation.Navigation
import com.advice.schedule.ui.navigation.navigate

@Composable
internal fun Organizations(navController: NavHostController, label: String?, id: Long?) {
    val viewModel = navController.navGraphViewModel<OrganizationsViewModel>()
    val state = viewModel.getState(id!!).collectAsState(initial = null).value
    com.advice.organizations.ui.screens.OrganizationsScreen(
        label = label ?: "",
        organizations = state,
        onBackPressed = {
            navController.popBackStack()
        },
        onOrganizationPressed = {
            navController.navigate(Navigation.Organization(it.id))
        },
    )
}


@Composable
internal fun Organization(
    navController: NavHostController,
    id: Long?,
) {
    val context = LocalContext.current

    val viewModel = navController.navGraphViewModel<OrganizationsViewModel>()

    val flow = remember(id) { viewModel.getOrganization(id) }
    val organization = flow.collectAsState(initial = null).value

    com.advice.organizations.ui.screens.OrganizationScreen(
        organization = organization,
        onBackPressed = {
            navController.popBackStack()
        },
        onLinkClicked = {
            (context as MainActivity).openLink(it)
        },
        onScheduleClicked = { id, label ->
            navController.navigate(Navigation.Schedule(label, listOf(id)))
        })
}
