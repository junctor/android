package com.advice.merch.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Merch
import com.advice.core.ui.MerchState
import com.advice.merch.R
import com.advice.merch.views.EditableMerchItem
import com.advice.merch.views.PromoSwitch
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.preview.MerchProvider
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchSummaryScreenView(
    state: MerchState,
    onQuantityChanged: (String, Int, String?) -> Unit,
    onBackPressed: () -> Unit,
    onDiscountApplied: (Boolean) -> Unit,
) {
    val list = state.elements.filter { it.quantity > 0 }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Merch") }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.Close, null)
            }
        })
    }) {
        MerchSummaryContents(
            list,
            state.hasDiscount,
            Modifier.padding(it),
            onQuantityChanged,
            onDiscountApplied
        )
    }
}

@Composable
fun MerchSummaryContents(
    list: List<Merch>,
    hasDiscount: Boolean,
    modifier: Modifier,
    onQuantityChanged: (String, Int, String?) -> Unit,
    onDiscountApplied: (Boolean) -> Unit,
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

        for (merch in list) {
            EditableMerchItem(merch, onQuantityChanged = {
                onQuantityChanged(merch.id, it, merch.selectedOption)
            })
        }

        PromoSwitch(
            title = "Goon Discount",
            description = "Must present Goon badge",
            checked = hasDiscount,
            onCheckedChange = onDiscountApplied
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val subtotal = getSubtotal(list)
            Text("Subtotal", style = MaterialTheme.typography.titleLarge)
            Text("$${subtotal} USD", style = MaterialTheme.typography.titleLarge)
        }
    }
}

fun getSubtotal(list: List<Merch>): Int {
    return list.sumOf { element ->
        element.discountedPrice ?: element.cost
    }
}


@LightDarkPreview
@Composable
fun MerchSummaryScreenViewPreview(@PreviewParameter(MerchProvider::class) state: MerchState) {
    ScheduleTheme {
        MerchSummaryScreenView(state, { _, _, _ -> }, {}, {})
    }
}