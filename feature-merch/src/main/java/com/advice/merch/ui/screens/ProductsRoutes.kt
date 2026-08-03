package com.advice.merch.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.advice.core.local.products.Product
import com.advice.merch.presentation.state.ProductsScreenState
import com.advice.merch.presentation.viewmodel.ProductsViewModel
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.screens.ErrorScreen

/**
 * Route-level composables for the merch destinations. The app module only wires
 * navigation callbacks and the activity-scoped [ProductsViewModel]; all state
 * handling (loading/error/lookup/brightness) lives here with the feature.
 */
@Composable
fun ProductsRoute(
    viewModel: ProductsViewModel,
    label: String,
    onSummaryClicked: () -> Unit,
    onProductClicked: (Product) -> Unit,
    onLearnMore: (documentId: Long) -> Unit,
    onBackPressed: () -> Unit,
) {
    val state = viewModel.state.collectAsState(ProductsScreenState.Loading).value

    ProductsScreen(
        label = label,
        state = state,
        onSummaryClicked = onSummaryClicked,
        onProductClicked = onProductClicked,
        onBackPressed = onBackPressed,
        onRetry = { viewModel.retry() },
        onLearnMore = {
            val merchDocument = (state as? ProductsScreenState.Success)?.data?.merchDocument
            if (merchDocument != null) {
                onLearnMore(merchDocument)
            }
        },
        onTagClicked = { viewModel.onTagClicked(it) },
        onClearFilters = { viewModel.clearFilters() },
        onDismiss = { viewModel.dismiss(it) },
    )
}

@Composable
fun ProductRoute(
    viewModel: ProductsViewModel,
    id: Long?,
    onBackPressed: () -> Unit,
) {
    when (val state = viewModel.state.collectAsState(ProductsScreenState.Loading).value) {
        ProductsScreenState.Loading -> {
            ProgressSpinner()
        }

        ProductsScreenState.Error -> {
            ErrorScreen(
                message = "Could not load merch",
                onRetry = { viewModel.retry() },
                onBackPress = onBackPressed,
            )
        }

        is ProductsScreenState.Success -> {
            val product = state.data.products.find { it.id == id }
            if (product == null) {
                ErrorScreen(
                    message = "Product not found",
                    onBackPress = onBackPressed,
                )
                return
            }
            ProductScreen(
                product = product,
                taxStatement = state.data.merchTaxStatement,
                canAdd = state.data.canAdd,
                onAddClicked = {
                    viewModel.addToCart(it)
                    onBackPressed()
                },
                onBackPressed = onBackPressed,
            )
        }
    }
}

@Composable
fun ProductsSummaryRoute(
    viewModel: ProductsViewModel,
    onBackPressed: () -> Unit,
) {
    when (val state = viewModel.state.collectAsState(ProductsScreenState.Loading).value) {
        ProductsScreenState.Loading -> {
            ProgressSpinner()
        }

        ProductsScreenState.Error -> {
            ErrorScreen(
                message = "Could not load merch",
                onRetry = { viewModel.retry() },
                onBackPress = onBackPressed,
            )
        }

        is ProductsScreenState.Success -> {
            MaxScreenBrightness()

            ProductsSummaryScreen(
                state = state.data,
                onQuantityChanged = { id, quantity, variant ->
                    viewModel.setQuantity(id, quantity, variant)
                },
                onBackPressed = onBackPressed,
            )
        }
    }
}

/** Maxes out screen brightness while the QR code is visible, restoring it on exit. */
@Composable
private fun MaxScreenBrightness() {
    val activity = LocalContext.current.findActivity() ?: return
    DisposableEffect(Unit) {
        val attributes = activity.window.attributes
        val previousBrightness = attributes.screenBrightness
        attributes.screenBrightness = 1.0f
        activity.window.attributes = attributes
        onDispose {
            attributes.screenBrightness = previousBrightness
            activity.window.attributes = attributes
        }
    }
}

private tailrec fun Context.findActivity(): Activity? =
    when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
