package com.advice.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationScreenView(hasCodeOfConduct: Boolean, hasSupport: Boolean, hasWifi: Boolean, onClick: (Int) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Information")
                },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Menu, null)
                    }
                }
            )
        },
    ) {

        Column(Modifier.padding(it)) {
            if (hasWifi) {
                Navigation("WiFi") {
                    onClick(-1)
                }
            }
            if (hasCodeOfConduct) {
                Navigation("Code of Conduct") {
                    onClick(-2)
                }
            }
            if (hasSupport) {
                Navigation("Help & Support") {
                    onClick(-3)
                }
            }
            Navigation("FAQ") {
                onClick(0)
            }
            Navigation("Locations") {
                onClick(1)
            }
            Navigation("Speakers") {
                onClick(2)
            }
            Navigation("Partners & Vendors") {
                onClick(3)
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
        Icon(Icons.Default.ArrowForward, null)
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationPreview() {
    MaterialTheme {
        Navigation("Help & Support") {}
    }
}

@Preview(showBackground = true)
@Composable
fun InformationScreenViewPreview() {
    MaterialTheme {
        InformationScreenView(hasCodeOfConduct = true, hasSupport = true, hasWifi = true) {

        }
    }
}