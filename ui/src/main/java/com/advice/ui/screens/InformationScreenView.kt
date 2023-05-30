package com.advice.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.ui.theme.ScheduleTheme
import com.shortstack.hackertracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationScreenView(
    hasCodeOfConduct: Boolean,
    hasSupport: Boolean,
    hasWifi: Boolean,
    onClick: (String) -> Unit,
    onBackPressed: () -> Unit
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
            if (hasWifi) {
                Navigation("WiFi") {
                    onClick("wifi")
                }
            }
            if (hasCodeOfConduct) {
                Navigation("Code of Conduct") {
                    onClick("code_of_conduct")
                }
            }
            if (hasSupport) {
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
            Navigation("Partners & Vendors") {
                onClick("partners_and_vendors")
            }
        }
    }
}

@Composable
fun Navigation(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier
            .clickable { onClick() }
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f))
        Icon(painterResource(id = R.drawable.baseline_arrow_back_ios_new_24), null, modifier = Modifier.rotate(180f))
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationPreview() {
    ScheduleTheme {
        Navigation("Help & Support") {}
    }
}

@Preview(showBackground = true)
@Composable
fun InformationScreenViewPreview() {
    ScheduleTheme {
        InformationScreenView(hasCodeOfConduct = true, hasSupport = true, hasWifi = true, {

        }, {})
    }
}