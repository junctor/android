package com.advice.products.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.core.local.Product
import com.advice.core.local.ProductSelection
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.QuantityAdjuster
import com.advice.products.ui.preview.ProductsProvider
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    product: Product,
    onAddClicked: (ProductSelection) -> Unit,
    onBackPressed: () -> Unit,
) {
    var quantity by remember {
        mutableStateOf(1)
    }

    val systemUiController = rememberSystemUiController()

    var selection by remember {
        mutableStateOf(if (!product.requiresSelection) product.variants.first().label else null)
    }

    val hasMedia = product.media.isNotEmpty()

    if (hasMedia) {
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
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.Close, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (hasMedia) Color.Transparent else MaterialTheme.colorScheme.surface,
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selection != null || !product.requiresSelection) {
                        onAddClicked(
                            ProductSelection(
                                id = product.id,
                                quantity = quantity,
                                selectionOption = selection,
                            )
                        )
                    }
                },
                Modifier
                    .padding(horizontal = 32.dp)
                    .fillMaxWidth(),
                shape = FloatingActionButtonDefaults.extendedFabShape
            ) {
                val optionCost =
                    if (product.selectedOption != null) product.variants.find { it.label == product.selectedOption }?.extraCost
                        ?: 0 else 0
                val cost = (product.baseCost + optionCost) * quantity
                Text("Add $quantity to cart ∙ US$${String.format("%.2f", cost / 100f)}")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        Product(product, quantity, selection, {
            selection = it
        }, {
            quantity = it
        }, Modifier.padding(it))
    }
}

@Composable
fun Product(
    product: Product,
    quantity: Int,
    selection: String?,
    onSelectionChanged: (String) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    modifier: Modifier,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        val hasMedia = product.media.isNotEmpty()
        if (hasMedia) {
            Box(
                Modifier
                    .background(Color.Black)
                    .fillMaxWidth()
            ) {

                AsyncImage(
                    model = product.media.first().url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
//                        .height(300.dp)
//                        .alpha(0.65f),
                    ,
                    contentScale = ContentScale.FillWidth
                )
                Box(
                    Modifier
                        .background(Color.Black)

                )
            }
        }

        Column(if (hasMedia) Modifier else modifier) {
            Column(
                Modifier.padding(16.dp)
            ) {
                Text(product.label, style = MaterialTheme.typography.labelLarge)
                val cost = String.format("%.2f", product.baseCost / 100f)
                Text("$$cost USD", style = MaterialTheme.typography.bodyMedium)
            }


            if (product.requiresSelection) {
                Row(Modifier.padding(16.dp)) {
                    Text("Options", Modifier.weight(1.0f))
                    Text("Required")
                }
            }
            for (option in product.variants) {
                Row(
                    Modifier
                        .clickable {
                            onSelectionChanged(option.label)
                        }
                        .defaultMinSize(minHeight = 64.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    val label = if (option.extraCost > 0) option.label + "  (+US" + String.format(
                        "%.2f",
                        option.extraCost / 100f
                    ) + ")" else option.label
                    Text(
                        label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier
                            .weight(
                                1.0f
                            )
                            .padding(16.dp)
                    )
                    RadioButton(
                        selected = option.label == selection,
                        onClick = { onSelectionChanged(option.label) })
                }
            }

            QuantityAdjuster(
                quantity = quantity,
                onQuantityChanged = onQuantityChanged,
                canDelete = false,
                Modifier.padding(16.dp)
            )

            Spacer(Modifier.height(64.dp + 8.dp))
        }
    }
}

@LightDarkPreview
@Composable
fun ProductScreenPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        ProductScreen(state.elements.first(), {}) {}
    }
}