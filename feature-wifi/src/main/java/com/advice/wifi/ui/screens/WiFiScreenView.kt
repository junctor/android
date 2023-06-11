package com.advice.wifi.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.components.Paragraph
import com.advice.wifi.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiScreen(onBackPressed: () -> Unit, onLinkClicked: (String) -> Unit) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("WiFi") }, navigationIcon =
        {
            IconButton(onClick = onBackPressed) {
                Icon(
                    painterResource(com.shortstack.hackertracker.R.drawable.baseline_arrow_back_ios_new_24),
                    contentDescription = null
                )
            }
        })
    }) {
        WifiScreenContent(Modifier.padding(it), onLinkClicked)
    }
}

@Composable
fun WifiScreenContent(modifier: Modifier, onLinkClicked: (String) -> Unit) {
    Column(
        modifier
    ) {
        Button(onClick = { onLinkClicked("https://www.google.com") }) {
            Text("Connect to WiFi")
        }
        val text = stringResource(R.string.wifi_instructions)
        Paragraph(
            text,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WifiScreenViewPreview() {
    ScheduleTheme {
        WifiScreen({}, {})
    }
}