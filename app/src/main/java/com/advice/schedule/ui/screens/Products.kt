package com.advice.schedule.ui.screens

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.advice.products.presentation.viewmodel.ProductsViewModel
import com.advice.products.ui.screens.ProductScreen
import com.advice.products.ui.screens.ProductsScreen
import com.advice.products.ui.screens.ProductsSummaryScreen
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.navigate

@Composable
fun Products(context: AppCompatActivity, navController: NavHostController) {
    val viewModel = viewModel<ProductsViewModel>(context)
    val state = viewModel.state.collectAsState(null).value

    ProductsScreen(
        state = state,
        onSummaryClicked = {
            navController.navigate(Navigation.ProductsSummary)
        },
        onProductClicked = {
            navController.navigate(Navigation.Product(it.id))
        },
        onBackPressed = {
            navController.popBackStack()
        },
        onLearnMore = {
            val merchDocument = state?.merchDocument
            if (merchDocument != null) {
                navController.navigate(Navigation.Document(merchDocument))
            }
        },
        onDismiss = {
            viewModel.dismiss()
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
    val state = viewModel.state.collectAsState(null).value ?: return
    val product = state.products.find { it.id == id } ?: return

    ProductScreen(
        product = product,
        canAdd = state.canAdd,
        onAddClicked = {
            viewModel.addToCart(it)
            navController.popBackStack()
        },
        onBackPressed = {
            navController.popBackStack()
        })
}

@Composable
fun ProductsSummary(context: AppCompatActivity,navController: NavHostController) {
    val viewModel = viewModel<ProductsViewModel>(context)
    val state = viewModel.state.collectAsState(null).value ?: return

    ProductsSummaryScreen(
        state = state,
        onQuantityChanged = { id, quantity, variant ->
            viewModel.setQuantity(id, quantity, variant)
        },
        onBackPressed = { navController.popBackStack() },
    )
}

