package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.core.network.report.ReportObjectType
import com.advice.documents.presentation.viewmodel.DocumentsScreenState
import com.advice.documents.presentation.viewmodel.DocumentsViewModel
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.navigation.onBackPressed
import com.advice.schedule.presentation.viewmodel.ReportViewModel
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.screens.ErrorScreen

@Composable
internal fun Document(
    navController: NavHostController,
    id: Long? = null,
) {
    val viewModel = navController.navGraphViewModel<DocumentsViewModel>()
    val reportViewModel = viewModel<ReportViewModel>()
    val state = viewModel.state.collectAsState(initial = DocumentsScreenState.Loading).value

    LaunchedEffect(id) {
        viewModel.get(id)
    }

    when (state) {
        is DocumentsScreenState.Error -> {
            ErrorScreen {
                navController.onBackPressed()
            }
        }

        DocumentsScreenState.Loading -> {
            ProgressSpinner()
        }

        is DocumentsScreenState.Success -> {
            com.advice.documents.ui.screens.DocumentScreen(
                document = state.document,
                onBackPressed = { navController.onBackPressed() },
                onReport = { message ->
                    reportViewModel.submit(
                        message = message,
                        objectType = ReportObjectType.DOCUMENT,
                        objectId = state.document.id,
                    )
                },
            )
        }
    }
}
