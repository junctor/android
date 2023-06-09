package com.advice.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.advice.core.ui.InformationState
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.views.Navigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationScreen(
    state: InformationState,
    onClick: (String) -> Unit,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Information")
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.Menu, null)
                    }
                }
            )
        },
    ) {

        Column(Modifier.padding(it)) {
            if (state.hasWifi) {
                Navigation("WiFi") {
                    onClick("wifi")
                }
            }
            if (state.hasCodeOfConduct) {
                Navigation("Code of Conduct") {
                    onClick("code_of_conduct")
                }
            }
            if (state.hasSupport) {
                Navigation("Help & Support") {
                    onClick("help_and_support")
                }
            }
            Navigation("FAQ") {
                onClick("faq")
            }
            Navigation("Locations") {
                onClick("locations")
            }
            Navigation("Speakers") {
                onClick("speakers")
            }
            if (state.hasVendors) {
                Navigation("Vendors") {
                    onClick("vendors")
                }
            }
            if (state.hasVillages) {
                Navigation("Villages") {
                    onClick("villages")
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
private fun InformationScreenViewPreview() {
    val state = InformationState(
        hasCodeOfConduct = true,
        hasWifi = true,
        hasSupport = true,
        hasVillages = true,
        hasVendors = true
    )

    ScheduleTheme {
        InformationScreen(state, {

        }, {})
    }
}