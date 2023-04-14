package com.advice.merch.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Merch
import com.advice.core.ui.MerchState
import com.advice.merch.R
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.preview.MerchProvider
import com.advice.ui.theme.ScheduleTheme
import com.advice.merch.views.MerchItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchScreenView(
    state: MerchState,
    onSummaryClicked: () -> Unit, onMerchClicked: (Merch) -> Unit,
) {
    val list = state.elements

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Merch") },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(painterResource(id = R.drawable.ic_filter), null)
                    }
                },


                )
        },
        floatingActionButton = {
            val itemCount = state.elements.sumOf { it.quantity }
            if (itemCount > 0) {
                FloatingActionButton(
                    onClick = onSummaryClicked,
                    Modifier.padding(horizontal = 32.dp).fillMaxWidth(),
                    shape = FloatingActionButtonDefaults.extendedFabShape
                ) {
                    Text("View Cart ($itemCount)")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) {
        MerchList(list, onMerchClicked, Modifier.padding(it))
    }
}

@Composable
fun MerchList(list: List<Merch>, onMerchClicked: (Merch) -> Unit, modifier: Modifier) {
    LazyColumn(modifier, contentPadding = PaddingValues(vertical = 32.dp)) {
        items(list) {
            MerchItem(it, onMerchClicked)
        }
    }
}

@LightDarkPreview
@Composable
fun MerchScreenViewPreview(@PreviewParameter(MerchProvider::class) state: MerchState) {
    ScheduleTheme {
        MerchScreenView(state, {}, {})
    }
}