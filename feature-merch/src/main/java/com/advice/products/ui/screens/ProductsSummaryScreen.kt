package com.advice.products.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductVariant
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.EditableProduct
import com.advice.products.ui.components.LegalLabel
import com.advice.products.ui.components.PriceLabel
import com.advice.products.ui.components.QRCodeImage
import com.advice.products.ui.preview.ProductsProvider
import com.advice.products.utils.toCurrency
import com.advice.ui.components.BackButton
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsSummaryScreen(
    state: ProductsState,
    onQuantityChanged: (Long, Int, ProductVariant?) -> Unit,
    onBackPressed: () -> Unit,
) {
    val list = state.cart

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Merch") }, navigationIcon = {
            BackButton(onBackPressed)
        })
    }) {
        Box(Modifier.padding(it)) {
            ProductsSummaryContent(
                list,
                state.json,
                state.merchTaxStatement,
                onQuantityChanged = onQuantityChanged,
            )
        }
    }
}

@Composable
private fun ProductsSummaryContent(
    list: List<Product>,
    json: String?,
    taxStatement: String?,
    modifier: Modifier = Modifier,
    onQuantityChanged: (Long, Int, ProductVariant?) -> Unit,
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
                        Image(
                            painter = painterResource(id = com.advice.ui.R.drawable.logo_glitch),
                            contentDescription = "logo",
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
            ) {
                Text("Nothing in your list", fontSize = 32.sp)
                Text("Add some items to generate a QR code", fontSize = 12.sp)
            }
        } else {

            for (merch in list) {
                EditableProduct(merch, onQuantityChanged = {
                    onQuantityChanged(merch.id, it, merch.selectedOption)
                })
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Subtotal")
                PriceLabel(
                    text = getSubtotal(list).toCurrency(showCents = true),
                )
            }
        }
        if (taxStatement != null) {
            LegalLabel(text = taxStatement)
        }
    }
}

private fun getSubtotal(list: List<Product>): Long =
    list.sumOf { element ->
        element.cost
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
    val state = state.copy(json = null)
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
