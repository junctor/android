package com.advice.products.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.products.Product
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.FeaturedProducts
import com.advice.products.ui.components.InformationCard
import com.advice.products.ui.components.ProductSquare
import com.advice.products.ui.preview.ProductsProvider
import com.advice.ui.components.EmptyMessage
import com.advice.ui.components.Label
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    state: ProductsState?,
    onSummaryClicked: () -> Unit,
    onProductClicked: (Product) -> Unit,
    onLearnMore: () -> Unit,
    onDismiss: () -> Unit,
    onBackPressed: () -> Unit,
) {
    val systemUiController = rememberSystemUiController()

    DisposableEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = Color.Black.copy(0.40f),
        )

        onDispose {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed, colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Black.copy(0.40f),
                        )
                    ) {
                        Icon(
                            painterResource(id = com.advice.ui.R.drawable.arrow_back),
                            "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onLearnMore, colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Black.copy(0.40f),
                        )
                    ) {
                        Icon(Icons.Outlined.Info, "Learn More", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                )
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
        Box {
            when {
                state == null -> {
                    Box(Modifier.padding(it)) {
                        ProgressSpinner()
                    }
                }

                state.products.isEmpty() -> {
                    Box(Modifier.padding(it)) {
                        EmptyMessage("Merch not found")
                    }
                }

                else -> {
                    ProductsScreenContent(
                        featured = state.featured,
                        list = state.products,
                        hasInformation = state.showMerchInformation,
                        onProductClicked = onProductClicked,
                        onLearnMore = onLearnMore,
                        onDismiss = onDismiss,
                    )
                }
            }
        }
    }
}

@Composable
fun ProductsScreenContent(
    featured: List<Product>,
    list: List<Product>,
    hasInformation: Boolean,
    onProductClicked: (Product) -> Unit,
    onLearnMore: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        if (featured.isNotEmpty()) {
            item {
                FeaturedProducts(products = featured, onProductClicked)
            }
            item {
                Label(text = "All Products", modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
        if (hasInformation) {
            item {
                InformationCard(
                    modifier = Modifier.padding(16.dp),
                    onLearnMore = onLearnMore,
                    onDismiss = onDismiss
                )
            }
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

@PreviewLightDark
@Composable
fun ProductsScreenPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        ProductsScreen(state, {}, {}, {}, {}, {})
    }
}
