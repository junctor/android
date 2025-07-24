package com.advice.schedule.ui.screens

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.products.presentation.state.ProductsScreenState
import com.advice.products.presentation.viewmodel.ProductsViewModel
import com.advice.products.ui.screens.ProductScreen
import com.advice.products.ui.screens.ProductsScreen
import com.advice.products.ui.screens.ProductsSummaryScreen
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.navigate
import com.advice.schedule.navigation.onBackPressed
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.screens.ErrorScreen

@Composable
fun Products(context: AppCompatActivity, navController: NavHostController, label: String) {
    val viewModel = viewModel<ProductsViewModel>(context)
    val state = viewModel.state.collectAsState(ProductsScreenState.Loading).value

    ProductsScreen(
        label = label,
        state = state,
        onSummaryClicked = {
            navController.navigate(Navigation.ProductsSummary)
        },
        onProductClicked = {
            navController.navigate(Navigation.Product(it.id))
        },
        onBackPressed = {
            navController.onBackPressed()
        },
        onLearnMore = {
            val merchDocument = (state as? ProductsScreenState.Success)?.data?.merchDocument
            if (merchDocument != null) {
                navController.navigate(Navigation.Document(merchDocument))
            }
        },
        onTagClicked = {
            viewModel.onTagClicked(it)
        },
        onDismiss = {
            viewModel.dismiss(it)
        },
    )
}

@Composable
fun Product(
    context: AppCompatActivity,
    navController: NavHostController,
    id: Long?,
) {
    val viewModel = viewModel<ProductsViewModel>(context)
    when (val state = viewModel.state.collectAsState(ProductsScreenState.Loading).value) {
        ProductsScreenState.Loading -> {
            ProgressSpinner()
        }

        ProductsScreenState.Error -> {
            ErrorScreen {
                // todo: implement
            }
        }

        is ProductsScreenState.Success -> {
            val product = state.data.products.find { it.id == id } ?: return
            ProductScreen(
                product = product,
                taxStatement = state.data.merchTaxStatement,
                canAdd = state.data.canAdd,
                onAddClicked = {
                    viewModel.addToCart(it)
                    navController.onBackPressed()
                },
                onBackPressed = {
                    navController.onBackPressed()
                }
            )
        }
    }
}

@Composable
fun ProductsSummary(context: AppCompatActivity, navController: NavHostController) {
    val viewModel = viewModel<ProductsViewModel>(context)
    when (val state = viewModel.state.collectAsState(ProductsScreenState.Loading).value) {
        ProductsScreenState.Loading -> {
            ProgressSpinner()
        }

        ProductsScreenState.Error -> {
            ErrorScreen {
                // todo: implement
            }
        }

        is ProductsScreenState.Success -> {
            LaunchedEffect(Unit) {
                val attributes = context.window.attributes
                attributes.screenBrightness = 1.0f
                context.window.attributes = attributes
            }

            ProductsSummaryScreen(
                state = state.data,
                onQuantityChanged = { id, quantity, variant ->
                    viewModel.setQuantity(id, quantity, variant)
                },
                onBackPressed = { navController.onBackPressed() },
            )
        }
    }
}
