package com.advice.merch.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.core.local.Merch
import com.advice.core.local.MerchSelection
import com.advice.core.ui.MerchState
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.preview.MerchProvider
import com.advice.ui.theme.ScheduleTheme
import com.advice.merch.views.QuantityView
import com.shortstack.hackertracker.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchItemScreenView(
    merch: Merch,
    onAddClicked: (MerchSelection) -> Unit,
    onBackPressed: () -> Unit,
) {
    var quantity by remember {
        mutableStateOf(1)
    }

    var selection by remember {
        mutableStateOf<String?>(null)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Merch") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.Close, null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selection != null || !merch.requiresSelection) {
                        onAddClicked(
                            MerchSelection(
                                id = merch.label,
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
                val optionCost = if(merch.selectedOption != null) merch.options.find { it.label == merch.selectedOption }?.extraCost ?: 0 else 0
                val cost = (merch.baseCost + optionCost) * quantity
                Text("Add $quantity to cart ∙ US$${String.format("%.2f", cost / 100f)}")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        MerchItem(merch, quantity, selection, {
            selection = it
        }, {
            quantity = it
        }, Modifier.padding(it))
    }
}

@Composable
fun MerchItem(
    merch: Merch,
    quantity: Int,
    selection: String?,
    onSelectionChanged: (String) -> Unit,
    onQuantityChanged: (Int) -> Unit,
    modifier: Modifier
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        if (merch.image != null) {
            Box(
                Modifier
                    .background(Color.White)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = merch.image, contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }

        Column(
            Modifier.padding(16.dp)
        ) {
            Text(merch.label, style = MaterialTheme.typography.labelLarge)
            val cost = String.format("%.2f", merch.baseCost / 100f)
            Text("$$cost USD", style = MaterialTheme.typography.bodyMedium)
        }


        if(merch.requiresSelection) {
            Row(Modifier.padding(16.dp)) {
                Text("Options", Modifier.weight(1.0f))
                Text("Required")
            }
        }
        for (option in merch.options) {
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

        QuantityView(
            quantity = quantity,
            onQuantityChanged = onQuantityChanged,
            canDelete = false,
            Modifier.padding(16.dp)
        )

        Spacer(Modifier.height(64.dp + 8.dp))
    }
}

@LightDarkPreview
@Composable
fun MerchItemScreenViewPreview(@PreviewParameter(MerchProvider::class) state: MerchState) {
    ScheduleTheme {
        MerchItemScreenView(state.elements.first(), {}) {}
    }
}