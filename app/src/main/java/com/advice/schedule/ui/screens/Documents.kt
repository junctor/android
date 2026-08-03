package com.advice.schedule.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.advice.documents.presentation.viewmodel.DocumentsViewModel
import com.advice.documents.ui.screens.DocumentRoute
import com.advice.feedback.network.models.ReportObjectType
import com.advice.feedback.presentation.viewmodel.ReportViewModel
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.navigation.onBackPressed
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun Document(
    navController: NavHostController,
    id: Long? = null,
) {
    val reportViewModel = koinViewModel<ReportViewModel>()

    DocumentRoute(
        viewModel = navController.navGraphViewModel<DocumentsViewModel>(),
        id = id,
        onBackPressed = { navController.onBackPressed() },
        onReport = { message, documentId ->
            reportViewModel.submit(
                message = message,
                objectType = ReportObjectType.DOCUMENT,
                objectId = documentId,
            )
        },
    )
}
