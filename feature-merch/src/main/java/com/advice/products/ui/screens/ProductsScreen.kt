package com.advice.products.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.products.Product
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.FeaturedProducts
import com.advice.products.ui.components.ProductRow
import com.advice.products.ui.components.ProductSquare
import com.advice.products.ui.preview.ProductsProvider
import com.advice.ui.components.EmptyMessage
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    state: ProductsState?,
    onSummaryClicked: () -> Unit,
    onProductClicked: (Product) -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Merch") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = com.advice.ui.R.drawable.arrow_back), "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (state != null) {
                val itemCount = state.cart.sumOf { it.quantity }
                if (itemCount > 0) {
                    FloatingActionButton(
                        onClick = onSummaryClicked,
                        Modifier
                            .padding(horizontal = 32.dp)
                            .fillMaxWidth(),
                        shape = FloatingActionButtonDefaults.extendedFabShape
                    ) {
                        Text("View List ($itemCount)")
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Box(Modifier.padding(it)) {
            when {
                state == null -> {
                    ProgressSpinner()
                }

                state.products.isEmpty() -> {
                    EmptyMessage("Merch not found")
                }

                else -> {
                    ProductsScreenContent(state.featured, state.products, onProductClicked)
                }
            }
        }
    }
}

@Composable
fun ProductsScreenContent(
    featured: List<Product>,
    list: List<Product>,
    onProductClicked: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier, contentPadding = PaddingValues(vertical = 16.dp)) {
        item {
            FeaturedProducts(products = featured, onProductClicked)
        }

        list.windowed(2, 2, partialWindows = true).forEachIndexed { index, products ->
            item(key = index) {
                ProductsRow(products, onProductClicked)
            }
        }
        item {
            Spacer(Modifier.height(64.dp))
        }
    }
}

@Composable
private fun ProductsRow(
    products: List<Product>,
    onProductClicked: (Product) -> Unit,
) {
    Row {
        for (product in products) {
            ProductSquare(product, onProductClicked, Modifier.weight(1f))
        }
        if (products.size == 1) Box(modifier = Modifier.weight(1f))
    }
}

@LightDarkPreview
@Composable
fun ProductsScreenPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        ProductsScreen(state, {}, {}, {})
    }
}
