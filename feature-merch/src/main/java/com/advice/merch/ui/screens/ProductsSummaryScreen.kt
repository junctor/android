package com.advice.merch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.core.local.products.ProductSelection
import com.advice.merch.R
import com.advice.merch.presentation.state.ProductsState
import com.advice.merch.presentation.viewmodel.cartSubtotalCents
import com.advice.merch.ui.components.EditableProduct
import com.advice.merch.ui.components.LegalLabel
import com.advice.merch.ui.components.PriceLabel
import com.advice.merch.ui.components.QRCodeImage
import com.advice.merch.ui.preview.ProductsProvider
import com.advice.merch.utils.toCurrency
import com.advice.ui.components.BackButton
import com.advice.ui.glitch.GlitchLogo
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsSummaryScreen(
    state: ProductsState,
    onQuantityChanged: (Long, Int, Long) -> Unit,
    onBackPressed: () -> Unit,
) {
    var outOfStockLabel by remember { mutableStateOf<String?>(null) }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Merch") }, navigationIcon = {
            BackButton(onBackPressed)
        })
    }) {
        Box(Modifier.padding(it)) {
            ProductsSummaryContent(
                state.cart,
                state.data,
                state.merchTaxStatement,
                onQuantityChanged = onQuantityChanged,
                onOutOfStockClick = { label ->
                    outOfStockLabel = label
                },
            )
        }
    }

    outOfStockLabel?.let { label ->
        AlertDialog(
            onDismissRequest = { outOfStockLabel = null },
            title = { Text(stringResource(R.string.out_of_stock_dialog_title)) },
            text = {
                Text(stringResource(R.string.out_of_stock_dialog_message, label))
            },
            confirmButton = {
                TextButton(onClick = { outOfStockLabel = null }) {
                    Text(stringResource(R.string.out_of_stock_dialog_dismiss))
                }
            },
        )
    }
}

@Composable
private fun ProductsSummaryContent(
    list: List<ProductSelection>,
    json: String?,
    taxStatement: String?,
    modifier: Modifier = Modifier,
    onQuantityChanged: (Long, Int, Long) -> Unit,
    onOutOfStockClick: (String) -> Unit,
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        Box(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Box(
                Modifier
                    .size(256.dp)
                    .align(Alignment.Center),
            ) {
                if (json != null) {
                    QRCodeImage(
                        json,
                        Modifier
                            .fillMaxSize()
                            .align(Alignment.Center)
                            .clip(roundedCornerShape),
                    )
                } else {
                    Surface(
                        Modifier
                            .fillMaxSize(),
                        shape = roundedCornerShape,
                    ) {
                        GlitchLogo(
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .background(Color.Black)
                                    .clip(roundedCornerShape),
                        )
                    }
                }
            }
        }

        if (list.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
            ) {
                Text("Nothing in your list", fontSize = 32.sp)
                Text("Add some items to generate a QR code", fontSize = 12.sp)
            }
        } else {
            for (merch in list) {
                EditableProduct(
                    product = merch,
                    onQuantityChanged = {
                        onQuantityChanged(merch.id, it, merch.variant.id)
                    },
                    onOutOfStockClick = {
                        onOutOfStockClick(merch.label)
                    },
                )
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Subtotal")
                PriceLabel(
                    text = cartSubtotalCents(list).toCurrency(showCents = true),
                )
            }
        }
        if (taxStatement != null) {
            LegalLabel(text = taxStatement)
        }
    }
}

@PreviewLightDark
@Composable
private fun ProductsSummaryScreenPreview(
    @PreviewParameter(ProductsProvider::class) state: ProductsState,
) {
    val state = state.copy(cart = state.cart.takeLast(2))
    ScheduleTheme {
        ProductsSummaryScreen(state, { _, _, _ -> }, {})
    }
}

@PreviewLightDark
@Composable
private fun ProductsSummaryScreenErrorPreview(
    @PreviewParameter(ProductsProvider::class) state: ProductsState,
) {
    val state = state.copy(data = null)
    ScheduleTheme {
        ProductsSummaryScreen(state, { _, _, _ -> }, {})
    }
}

@PreviewLightDark
@Composable
private fun ProductsSummaryContentPreview(
    @PreviewParameter(ProductsProvider::class) state: ProductsState,
) {
    val state = state.copy(cart = emptyList())
    ScheduleTheme {
        ProductsSummaryScreen(state, { _, _, _ -> }, {})
    }
}
