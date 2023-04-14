package com.advice.merch.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
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
    onSummaryClicked: () -> Unit, onMerchClicked: (MerchSelection) -> Unit,
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
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Close, null)
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selection != null || merch.options.isEmpty()) {
                        onMerchClicked(
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
                Text("Add to Cart")
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
        if (merch.hasImage) {
            Box(
                Modifier
                    .background(Color.White)
                    .size(128.dp)
            ) {
                Image(painterResource(R.drawable.doggo), null)
            }
        }

        Column(Modifier.padding(16.dp)) {
            Text(merch.label, style = MaterialTheme.typography.labelLarge)
            Text("$${merch.baseCost} USD", style = MaterialTheme.typography.bodyMedium)
        }


        for (option in merch.options) {
            Row(Modifier.clickable {
                onSelectionChanged(option)
            }) {
                Text(
                    option, style = MaterialTheme.typography.bodyMedium, modifier = Modifier
                        .weight(
                            1.0f
                        )
                        .padding(16.dp)
                )
                RadioButton(selected = option == selection, onClick = { onSelectionChanged(option) })
            }
        }

        QuantityView(
            count = quantity,
            onRemoveClicked = { onQuantityChanged(quantity - 1) },
            onAddClicked = { onQuantityChanged(quantity + 1) },
            canDelete = false
        )
    }


}

@LightDarkPreview
@Composable
fun MerchItemScreenViewPreview(@PreviewParameter(MerchProvider::class) state: MerchState) {
    ScheduleTheme {
        MerchItemScreenView(state.elements.first(), {}, {})
    }
}