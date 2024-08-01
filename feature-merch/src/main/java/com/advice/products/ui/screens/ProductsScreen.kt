package com.advice.products.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.core.local.Tag
import com.advice.core.local.products.Product
import com.advice.products.presentation.state.ProductsScreenState
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.DismissibleInformation
import com.advice.products.ui.components.InformationCard
import com.advice.products.ui.components.LabelButton
import com.advice.products.ui.components.LegalLabel
import com.advice.products.ui.components.ProductFilterBottomSheet
import com.advice.products.ui.components.ProductsRow
import com.advice.products.ui.preview.ProductsProvider
import com.advice.ui.components.BackButton
import com.advice.ui.components.EmptyMessage
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    label: String,
    state: ProductsScreenState,
    onSummaryClicked: () -> Unit,
    onProductClicked: (Product) -> Unit,
    onLearnMore: () -> Unit,
    onDismiss: (DismissibleInformation) -> Unit,
    onTagClicked: (Tag) -> Unit,
    onBackPressed: () -> Unit,
) {
    var showing by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(label)
                },
                navigationIcon = {
                    BackButton(onBackPressed)
                },
                actions = {
                    if (state is ProductsScreenState.Success && state.data.merchDocument != null) {
                        IconButton(
                            onClick = onLearnMore
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "Learn More",
                            )
                        }
                    }
                    IconButton(onClick = {
                        showing = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Filter",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        },
    ) {
        Box(Modifier.padding(it)) {
            when (state) {
                ProductsScreenState.Loading -> {
                    ProgressSpinner()
                }

                ProductsScreenState.Error -> {
                    EmptyMessage("Merch not found")
                }

                is ProductsScreenState.Success -> {
                    Box {
                        ProductsScreenContent(
                            groups = state.data.groups,
                            informationList = state.data.informationList,
                            taxStatement = state.data.merchTaxStatement,
                            onProductClicked = onProductClicked,
                            onLearnMore = onLearnMore,
                            onDismiss = onDismiss,
                        )

                        val itemCount = state.data.cart.sumOf { it.quantity }
                        if (itemCount > 0) {
                            LabelButton(
                                label = "View List ($itemCount)",
                                onClick = onSummaryClicked,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp)
                                    .fillMaxWidth(),
                            )
                        }

                        if (showing) {
                            ProductFilterBottomSheet(
                                tagTypes = state.data.productVariantTagTypes,
                                onDismiss = {
                                    showing = false
                                },
                                onClick = {
                                    onTagClicked(it)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductsScreenContent(
    groups: Map<Tag, List<Product>>,
    informationList: List<DismissibleInformation>,
    taxStatement: String?,
    onProductClicked: (Product) -> Unit,
    onLearnMore: () -> Unit,
    onDismiss: (DismissibleInformation) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier.padding(horizontal = 16.dp)) {
        items(informationList) { dismissibleInformation ->
            InformationCard(
                information = dismissibleInformation,
                onLearnMore = onLearnMore,
                onDismiss = {
                    onDismiss(dismissibleInformation)
                },
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        for (group in groups) {
            item(key = group.key.id) {
                SectionHeader(group.key.label)
            }
            group.value.windowed(2, 2, partialWindows = true).forEachIndexed { index, products ->
                item(key = "${group.key.id}_$index") {
                    ProductsRow(products, onProductClicked)
                }
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

@Composable
private fun SectionHeader(label: String) {
    Text(
        text = label,
        modifier = Modifier
            .padding(top = 24.dp)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
    )
}

@PreviewLightDark
@Composable
private fun ProductsScreenPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        ProductsScreen(
            label = "Merch",
            state = ProductsScreenState.Success(state),
            onSummaryClicked = {},
            onProductClicked = {},
            onLearnMore = {},
            onDismiss = {},
            onTagClicked = {},
            onBackPressed = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun ProductsScreenLoadingPreview() {
    ScheduleTheme {
        ProductsScreen(
            label = "Merch",
            state = ProductsScreenState.Loading,
            onSummaryClicked = {},
            onProductClicked = {},
            onLearnMore = {},
            onDismiss = {},
            onTagClicked = {},
            onBackPressed = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun ProductsScreenError() {
    ScheduleTheme {
        ProductsScreen(
            label = "Merch",
            state = ProductsScreenState.Error,
            onSummaryClicked = {},
            onProductClicked = {},
            onLearnMore = {},
            onDismiss = {},
            onTagClicked = {},
            onBackPressed = {}
        )
    }
}
