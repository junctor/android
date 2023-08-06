package com.advice.products.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductMedia
import com.advice.core.local.products.ProductSelection
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.LowStockLabel
import com.advice.products.ui.components.OutOfStockLabel
import com.advice.products.ui.components.PagerDots
import com.advice.products.ui.components.QuantityAdjuster
import com.advice.products.ui.preview.ProductsProvider
import com.advice.products.utils.toCurrency
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shortstack.core.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    product: Product,
    canAdd: Boolean,
    onAddClicked: (ProductSelection) -> Unit,
    onBackPressed: () -> Unit,
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Black.copy(0.40f),
    )

    var quantity by remember {
        mutableStateOf(1)
    }

    var selection by remember {
        mutableStateOf(if (!product.requiresSelection) product.variants.first().label else null)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                actions = {
                    IconButton(
                        onClick = onBackPressed,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Black.copy(0.40f),
                        ),
                    ) {
                        Icon(
                            Icons.Default.Close,
                            "Close",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                )
            )
        },
        floatingActionButton = {

        },
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Product(
            product = product,
            canAdd = canAdd,
            quantity = quantity,
            selection = selection,
            onAddClicked = onAddClicked,
            onSelectionChanged = {
                selection = it
            },
            onQuantityChanged = {
                quantity = it
            },
            modifier = Modifier.padding(it)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Product(
    product: Product,
    canAdd: Boolean,
    quantity: Int,
    selection: String?,
    onAddClicked: (ProductSelection) -> Unit,
    onSelectionChanged: (String) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    modifier: Modifier,
) {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Box(
            Modifier
                .aspectRatio(0.9f)
                .clip(RoundedCornerShape(8.dp))
        ) {

            val media = product.media.firstOrNull()
            if (media != null) {
                ImageGallery(product.media)
            } else {
                PlaceHolderImage()
            }

            if (product.stockStatus == StockStatus.LOW_STOCK) {
                LowStockLabel(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                )
            }
            if (product.stockStatus == StockStatus.OUT_OF_STOCK) {
                OutOfStockLabel(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomEnd)
                )
            }
        }
        Column {
            Column(
                Modifier.padding(16.dp)
            ) {
                Text(product.label, fontWeight = FontWeight.SemiBold)
                Text(product.baseCost.toCurrency())
            }

            if (product.requiresSelection) {
                Row(Modifier.padding(16.dp)) {
                    Text(
                        stringResource(com.advice.products.R.string.variants_label),
                        Modifier.weight(1.0f)
                    )
                    if (canAdd) {
                        Text(stringResource(com.advice.products.R.string.variant_required))
                    }
                }
            }
            if (product.requiresSelection) {
                for (option in product.variants) {
                    val inStock = option.stockStatus != StockStatus.OUT_OF_STOCK
                    Row(
                        Modifier
                            .clickable(enabled = inStock) {
                                onSelectionChanged(option.label)
                            }
                            .alpha(if (inStock) 1.0f else 0.64f)
                            .defaultMinSize(minHeight = 64.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1.0f)
                                .padding(16.dp)
                        ) {
                            val label =
                                if (option.extraCost > 0) option.label + stringResource(
                                    com.advice.products.R.string.label_with_variant,
                                    option.extraCost.toCurrency()
                                ) else option.label
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                            if (option.stockStatus == StockStatus.LOW_STOCK) {
                                Spacer(modifier = Modifier.height(4.dp))
                                LowStockLabel()
                            }
                            if (option.stockStatus == StockStatus.OUT_OF_STOCK) {
                                Spacer(modifier = Modifier.height(4.dp))
                                OutOfStockLabel()
                            }
                        }
                        if (canAdd) {
                            RadioButton(
                                selected = option.label == selection,
                                onClick = { onSelectionChanged(option.label) },
                                enabled = inStock,
                            )
                        }
                    }
                }
            }

            if (canAdd && product.stockStatus != StockStatus.OUT_OF_STOCK) {
                QuantityAdjuster(
                    quantity = quantity,
                    onQuantityChanged = onQuantityChanged,
                    canDelete = false,
                    Modifier.padding(16.dp)
                )

                val enabled = selection != null
                Button(
                    onClick = {
                        if (enabled || !product.requiresSelection) {
                            onAddClicked(
                                ProductSelection(
                                    id = product.id,
                                    quantity = quantity,
                                    selectionOption = selection,
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxWidth(),
                    enabled = enabled,
                ) {
                    val label = if (enabled) {
                        val optionCost =
                            product.variants.find { it.label == selection }?.extraCost ?: 0
                        val cost = (product.baseCost + optionCost) * quantity
                        stringResource(
                            com.advice.products.R.string.add_to_cart,
                            quantity,
                            cost.toCurrency()
                        )
                    } else {
                        stringResource(com.advice.products.R.string.add_to_cart_disabled)
                    }

                    Text(label, modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            Spacer(Modifier.height(64.dp + 8.dp))
        }
    }
}

@Composable
private fun PlaceHolderImage() {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.50f))
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_glitch),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ImageGallery(media: List<ProductMedia>) {
    Box {
        val scope = rememberCoroutineScope()
        val state = rememberPagerState {
            media.size
        }

        LaunchedEffect(Unit) {
            scope.launch {
                while (true) {
                    delay(4000L)
                    with(state) {
                        animateScrollToPage((currentPage + 1) % pageCount)
                    }
                }
            }
        }

        HorizontalPager(state = state) {
            AsyncImage(
                model = media[it].url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        PagerDots(
            state, modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

@LightDarkPreview
@Composable
fun ProductScreenPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        ProductScreen(state.products.first(), true, {}) {}
    }
}
