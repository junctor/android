package com.advice.documents.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.advice.documents.presentation.viewmodel.DocumentsScreenState
import com.advice.documents.presentation.viewmodel.DocumentsViewModel
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.screens.ErrorScreen

/**
 * Route-level composable for a single document. Owns fetching and
 * loading/error/content state handling; the app module supplies navigation
 * and the cross-feature report callback.
 */
@Composable
fun DocumentRoute(
    viewModel: DocumentsViewModel,
    id: Long?,
    onBackPressed: () -> Unit,
    onReport: (message: String, documentId: Long) -> Unit,
) {
    val state = viewModel.state.collectAsState(initial = DocumentsScreenState.Loading).value

    LaunchedEffect(id) {
        viewModel.get(id)
    }

    when (state) {
        is DocumentsScreenState.Error -> {
            ErrorScreen(onBackPress = onBackPressed)
        }

        DocumentsScreenState.Loading -> {
            ProgressSpinner()
        }

        is DocumentsScreenState.Success -> {
            DocumentScreen(
                document = state.document,
                onBackPressed = onBackPressed,
                onReport = { message ->
                    onReport(message, state.document.id)
                },
            )
        }
    }
}
