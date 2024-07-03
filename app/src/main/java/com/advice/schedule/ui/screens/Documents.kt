package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.advice.documents.presentation.viewmodel.DocumentsViewModel
import com.advice.documents.presentation.viewmodel.DocumentsScreenState
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.screens.ErrorScreen

@Composable
internal fun Document(navController: NavHostController, id: Long? = null) {
    val viewModel = navController.navGraphViewModel<DocumentsViewModel>()
    val state = viewModel.state.collectAsState(initial = DocumentsScreenState.Loading).value

    when (state) {
        is DocumentsScreenState.Error -> {
            ErrorScreen {

            }
        }

        DocumentsScreenState.Loading -> {
            ProgressSpinner()
        }

        is DocumentsScreenState.Success -> {
            val document = state.documents.find { it.id == id } ?: error("Document not found: $id")
            com.advice.documents.ui.screens.DocumentScreen(document = document,
                onBackPressed = { navController.popBackStack() })
        }
    }
}
