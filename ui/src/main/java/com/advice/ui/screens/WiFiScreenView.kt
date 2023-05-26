package com.advice.ui.screens

import androidx.compose.foundation.layout.padding
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
import com.advice.ui.views.Paragraph
import com.shortstack.hackertracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiScreenView(onBackPressed: () -> Unit, onLinkClicked: (String) -> Unit) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("WiFi") }, navigationIcon =
        {
            IconButton(onClick = onBackPressed) {
                Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
            }
        })
    }) {
        WifiScreenContent(Modifier.padding(it), onLinkClicked)
    }
}

@Composable
fun WifiScreenContent(modifier: Modifier, onLinkClicked: (String) -> Unit) {
    val text = stringResource(R.string.wifi_instructions)
    Paragraph(
        text,
        modifier
    )
}

@Preview(showBackground = true)
@Composable
fun WifiScreenViewPreview() {
    ScheduleTheme {
        WifiScreenView({}, {})
    }
}