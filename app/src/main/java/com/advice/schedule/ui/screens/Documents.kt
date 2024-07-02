package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.advice.documents.presentation.viewmodel.DocumentsViewModel
import com.advice.schedule.extensions.navGraphViewModel

@Composable
internal fun Document(navController: NavHostController, id: Long? = null) {
    val viewModel = navController.navGraphViewModel<DocumentsViewModel>()
    val documents = viewModel.documents.collectAsState(initial = null).value ?: return
    val document = documents.find { it.id == id } ?: return
    com.advice.documents.ui.screens.DocumentScreen(document = document,
        onBackPressed = { navController.popBackStack() })
}
