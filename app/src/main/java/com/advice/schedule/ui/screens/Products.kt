package com.advice.schedule.ui.screens

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.advice.merch.presentation.viewmodel.ProductsViewModel
import com.advice.merch.ui.screens.ProductRoute
import com.advice.merch.ui.screens.ProductsRoute
import com.advice.merch.ui.screens.ProductsSummaryRoute
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.navigateTo
import com.advice.schedule.navigation.onBackPressed
import org.koin.androidx.compose.koinViewModel

@Composable
fun Products(
    context: AppCompatActivity,
    navController: NavHostController,
    label: String,
) {
    ProductsRoute(
        viewModel = koinViewModel<ProductsViewModel>(viewModelStoreOwner = context),
        label = label,
        onSummaryClicked = { navController.navigateTo(Navigation.ProductsSummary) },
        onProductClicked = { navController.navigateTo(Navigation.Product(it.id)) },
        onLearnMore = { navController.navigateTo(Navigation.Document(it)) },
        onBackPressed = { navController.onBackPressed() },
    )
}

@Composable
fun Product(
    context: AppCompatActivity,
    navController: NavHostController,
    id: Long?,
) {
    ProductRoute(
        viewModel = koinViewModel<ProductsViewModel>(viewModelStoreOwner = context),
        id = id,
        onBackPressed = { navController.onBackPressed() },
    )
}

@Composable
fun ProductsSummary(
    context: AppCompatActivity,
    navController: NavHostController,
) {
    ProductsSummaryRoute(
        viewModel = koinViewModel<ProductsViewModel>(viewModelStoreOwner = context),
        onBackPressed = { navController.onBackPressed() },
    )
}
