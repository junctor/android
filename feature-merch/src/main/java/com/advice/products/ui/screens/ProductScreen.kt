package com.advice.products.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductSelection
import com.advice.core.local.products.ProductVariant
import com.advice.core.local.products.ProductVariantSelection
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.LabelButton
import com.advice.products.ui.components.LegalLabel
import com.advice.products.ui.components.LowStockLabel
import com.advice.products.ui.components.OutOfStockLabel
import com.advice.products.ui.components.PriceLabel
import com.advice.products.ui.components.QuantityAdjuster
import com.advice.products.ui.components.VariantsBottomSheet
import com.advice.products.ui.preview.ProductsProvider
import com.advice.products.utils.toCurrency
import com.advice.ui.R
import com.advice.ui.components.BackButton
import com.advice.ui.components.ImageGallery
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    product: Product,
    taxStatement: String?,
    canAdd: Boolean,
    onAddClicked: (ProductVariantSelection) -> Unit,
    onBackPressed: () -> Unit,
) {
    var quantity by remember {
        mutableStateOf(1)
    }

    var selection by remember {
        // if the product only has 1 variant, select it by default.
        val variant = if (product.variants.size == 1) product.variants.first() else null
        mutableStateOf(variant)
    }

    var showing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    BackButton(onBackPressed)
                },
                title = { },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
            )
        },

        ) {
        Product(
            product = product,
            taxStatement = taxStatement,
            canAdd = canAdd,
            quantity = quantity,
            selection = selection,
            onAddClicked = onAddClicked,
            onExpandBottomSheet = {
                showing = true
            },
            onQuantityChanged = {
                quantity = it
            },
            modifier = Modifier.padding(it),
        )

        if (showing) {
            VariantsBottomSheet(
                variants = product.variants,
                selection = selection,
                onVariantSelected = {
                    selection = it
                    showing = false
                },
                onDismiss = { showing = false },
                canAdd = canAdd,
            )
        }
    }
}


@Composable
fun Product(
    product: Product,
    taxStatement: String?,
    canAdd: Boolean,
    quantity: Int,
    selection: ProductVariant?,
    onAddClicked: (ProductVariantSelection) -> Unit,
    onExpandBottomSheet: () -> Unit,
    onQuantityChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        Box {
            val media = product.media.firstOrNull()
            if (media != null) {
                ImageGallery(product.media.map { it.url })
            } else {
                PlaceHolderImage()
            }
        }
        Column {
            // Product Info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(product.label, fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val currentCost = selection?.price ?: product.baseCost
                    PriceLabel(currentCost.toCurrency(showCents = true))
                    Spacer(Modifier.weight(1f))
                    if (product.stockStatus == StockStatus.LOW_STOCK) {
                        LowStockLabel()
                    }
                    if (product.stockStatus == StockStatus.OUT_OF_STOCK) {
                        OutOfStockLabel()
                    }
                }
            }

            // Variants
            if (product.requiresSelection) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ProductRow(
                    label = "Variant", modifier = Modifier.clickable(onClick = onExpandBottomSheet)
                ) {
                    val label = selection?.label
                        ?: stringResource(com.advice.products.R.string.select_variant)
                    Text(label, modifier = Modifier.padding(end = 16.dp))
                }
            }

            // Quantity + Add
            if (canAdd && product.stockStatus != StockStatus.OUT_OF_STOCK) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                ProductRow("Quantity") {
                    QuantityAdjuster(
                        quantity = quantity,
                        onQuantityChanged = onQuantityChanged,
                        canDelete = false,
                    )
                }

                FooterButton(selection, product, onAddClicked, quantity)
            }

            if (taxStatement != null) {
                LegalLabel(text = taxStatement)
            }

            Spacer(Modifier.height(64.dp + 8.dp))
        }
    }
}

@Composable
private fun ProductRow(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.width(16.dp))
        content()
    }
}

@Composable
private fun FooterButton(
    selection: ProductVariant?,
    product: Product,
    onAddClicked: (ProductVariantSelection) -> Unit,
    quantity: Int
) {
    val enabled = !product.requiresSelection || selection != null
    val label = if (enabled) {
        stringResource(com.advice.products.R.string.add_to_cart)
    } else {
        stringResource(com.advice.products.R.string.add_to_cart_disabled)
    }

    LabelButton(
        label = label,
        onClick = {
            if (selection != null) {
                onAddClicked(
                    ProductVariantSelection(
                        id = product.id,
                        quantity = quantity,
                        variant = selection.id,
                    ),
                )
            }
        },
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        enabled = enabled,
    )
}

@Composable
private fun PlaceHolderImage() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.50f))
            .padding(16.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_glitch),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@PreviewLightDark
@Composable
private fun ProductScreenPreview(
    @PreviewParameter(ProductsProvider::class) state: ProductsState,
) {
    ScheduleTheme {
        ProductScreen(
            product = state.products.first(),
            taxStatement = state.merchTaxStatement,
            canAdd = true,
            onAddClicked = {},
            onBackPressed = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun ProductScreenVariantSelectedPreview(
    @PreviewParameter(ProductsProvider::class) state: ProductsState,
) {
    ScheduleTheme {
        val product = state.products.first()
        ProductScreen(
            product = product.copy(),
            taxStatement = state.merchTaxStatement,
            canAdd = true,
            onAddClicked = {},
            onBackPressed = {},
        )
    }
}
