package com.advice.products.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Product
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.ProductView
import com.advice.products.ui.preview.ProductsProvider
import com.advice.ui.components.EmptyView
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

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
                        Icon(Icons.Default.ArrowBack, "Back")
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
                        Text("View Cart ($itemCount)")
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

                state.elements.isEmpty() -> {
                    EmptyView("Merch not found")
                }

                else -> {
                    ProductsScreenContent(state.elements, onProductClicked)
                }
            }
        }
    }
}

@Composable
fun ProductsScreenContent(
    list: List<Product>,
    onProductClicked: (Product) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier, contentPadding = PaddingValues(vertical = 16.dp)) {
        items(list) {
            ProductView(it, onProductClicked)
        }
        item {
            Spacer(Modifier.height(64.dp))
        }
    }
}

@LightDarkPreview
@Composable
fun ProductsScreenPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        ProductsScreen(state, {}, {}, {})
    }
}