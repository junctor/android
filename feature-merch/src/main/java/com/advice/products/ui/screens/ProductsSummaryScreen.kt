package com.advice.products.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Product
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.EditableProduct
import com.advice.products.ui.components.QRCodeImage
import com.advice.products.ui.preview.ProductsProvider
import com.advice.ui.components.EmptyMessage
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsSummaryScreen(
    state: ProductsState,
    onQuantityChanged: (Long, Int, String?) -> Unit,
    onBackPressed: () -> Unit,
) {
    val list = state.cart

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Merch") }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.Close, null)
            }
        })
    }) {
        if (list.isEmpty()) {
            EmptyMessage(message = "Merch not found", modifier = Modifier.padding(it))
        } else {
            ProductsSummaryContent(
                list,
                state.json,
                Modifier.padding(it),
                onQuantityChanged,
            )
        }
    }
}

@Composable
fun ProductsSummaryContent(
    list: List<Product>,
    json: String?,
    modifier: Modifier,
    onQuantityChanged: (Long, Int, String?) -> Unit,
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        if (json != null) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                QRCodeImage(
                    json, Modifier
                        .size(256.dp)
                        .align(Alignment.Center)
                )
            }
        }

        for (merch in list) {
            EditableProduct(merch, onQuantityChanged = {
                onQuantityChanged(merch.id, it, merch.selectedOption)
            })
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val subtotal = getSubtotal(list)
            Text("Subtotal", style = MaterialTheme.typography.titleLarge)
            Text(
                "$${String.format("%.2f", subtotal)} USD",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

fun getSubtotal(list: List<Product>): Float {
    return list.sumOf { element ->
        element.cost.toInt()
    } / 100f
}


@LightDarkPreview
@Composable
fun ProductsSummaryScreenPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        ProductsSummaryScreen(state, { _, _, _ -> }, {})
    }
}