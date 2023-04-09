package com.advice.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Merch
import com.advice.core.ui.MerchState
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.preview.MerchProvider
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.views.QuantityView
import com.shortstack.hackertracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchSummaryScreenView(
    list: List<Merch>, onRemoveClicked: (Merch) -> Unit,
    onAddClicked: (Merch) -> Unit, onBackPressed: () -> Unit
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Merch") }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.Close, null)
            }
        })
    }) {
        MerchSummaryContents(list, Modifier.padding(it), onRemoveClicked, onAddClicked)
    }
}

@Composable
fun MerchSummaryContents(
    list: List<Merch>,
    modifier: Modifier,
    onRemoveClicked: (Merch) -> Unit,
    onAddClicked: (Merch) -> Unit
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth()) {
            Icon(
                painterResource(id = R.drawable.ic_qr_code), null,
                Modifier
                    .size(256.dp)
                    .align(Alignment.Center)
            )
        }

        val list = list.filter { it.count > 0 }
        for (merch in list) {
            EditableMerchItem(merch,
                onRemoveClicked = {
                    onRemoveClicked(merch)
                }, onAddClicked = {
                    onAddClicked(merch)
                })
        }

        Row(Modifier.padding(16.dp)) {
            Column(Modifier.weight(1.0f)) {
                Text("Goon Discount")
                Text("Must present Goon badge")
            }
            Switch(checked = false, onCheckedChange = {

            })
        }


        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val subtotal = list.sumOf { it.cost * it.count }
            Text("Subtotal", style = MaterialTheme.typography.titleLarge)
            Text("$${subtotal} USD", style = MaterialTheme.typography.titleLarge)
        }
    }
}

@Composable
fun EditableMerchItem(merch: Merch, onRemoveClicked: () -> Unit, onAddClicked: () -> Unit) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Row(
            Modifier
                .defaultMinSize(minHeight = 64.dp)
        ) {
            Column(Modifier.weight(1.0f)) {
                Text(
                    merch.label,
                    style = MaterialTheme.typography.labelLarge
                )
                if (merch.sizes.isNotEmpty()) {
                    Text(merch.sizes.first())
                }
            }

            if (merch.image) {
                Box(
                    Modifier
                        .background(Color.White)
                        .size(48.dp)
                ) {
                    Image(painterResource(R.drawable.doggo), null)
                }
            }
        }
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuantityView(merch.count, onRemoveClicked, onAddClicked)
            Text("$${merch.cost} USD", style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@LightDarkPreview
@Composable
fun MerchSummaryScreenViewPreview(@PreviewParameter(MerchProvider::class) state: MerchState) {
    ScheduleTheme {
        MerchSummaryScreenView(state.elements.filter { it.count > 0 }, {}, {}, {})
    }
}