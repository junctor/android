package com.advice.products.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.products.Product
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.DismissibleInformation
import com.advice.products.ui.components.InformationCard
import com.advice.products.ui.components.LegalLabel
import com.advice.products.ui.components.ProductsRow
import com.advice.products.ui.preview.ProductsProvider
import com.advice.ui.components.EmptyMessage
import com.advice.ui.components.Label
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    state: ProductsState?,
    onSummaryClicked: () -> Unit,
    onProductClicked: (Product) -> Unit,
    onLearnMore: () -> Unit,
    onDismiss: (DismissibleInformation) -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed
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
                        onClick = onLearnMore
                    ) {
                        Icon(Icons.Outlined.Info, "Learn More", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(

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
        Box(Modifier.padding(it)) {
            when {
                state == null -> {
                    ProgressSpinner()
                }

                state.products.isEmpty() -> {
                    EmptyMessage("Merch not found")
                }

                else -> {
                    ProductsScreenContent(
                        list = state.products,
                        informationList = state.informationList,
                        mandatoryAcknowledgement = state.merchMandatoryAcknowledgement,
                        taxStatement = state.merchTaxStatement,
                        onProductClicked = onProductClicked,
                        onLearnMore = onLearnMore,
                        onDismiss = onDismiss,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun ProductsScreenContent(
    list: List<Product>,
    informationList: List<DismissibleInformation>,
    mandatoryAcknowledgement: String? = null,
    taxStatement: String? = null,
    onProductClicked: (Product) -> Unit,
    onLearnMore: () -> Unit,
    onDismiss: (DismissibleInformation) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        items(informationList) { dismissibleInformation ->
            InformationCard(
                information = dismissibleInformation,
                onLearnMore = onLearnMore,
                onDismiss = {
                    onDismiss(dismissibleInformation)
                },
                modifier = Modifier.padding(16.dp)
            )
        }

        item {
            Label(text = "All Products", modifier = Modifier.padding(horizontal = 16.dp))
        }
        
        list.windowed(2, 2, partialWindows = true).forEachIndexed { index, products ->
            item(key = index) {
                ProductsRow(products, onProductClicked)
            }
        }
        if (taxStatement != null) {
            item {
                LegalLabel(text = taxStatement)
            }
        }

        item {
            Spacer(Modifier.height(64.dp))
        }
    }
}

@PreviewLightDark
@Composable
private fun ProductsScreenPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        ProductsScreen(
            state = state,
            onSummaryClicked = {},
            onProductClicked = {},
            onLearnMore = {},
            onDismiss = {},
            onBackPressed = {}
        )
    }
}
