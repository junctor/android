package com.advice.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.ui.theme.ScheduleTheme
import com.shortstack.hackertracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiScreenView(onBackPressed: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("WiFi") }, navigationIcon =
        {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        })
    }) {
        WifiScreenContent(Modifier.padding(it))
    }
}

@Composable
fun WifiScreenContent(modifier: Modifier) {
    val text = stringResource(R.string.wifi_instructions)
    val string = buildAnnotatedString {
        val s = text.replace("<br/>", "\n")
        append(s)
    }
    Text(
        string,
        modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    )
}

@Preview(showBackground = true)
@Composable
fun WifiScreenViewPreview() {
    ScheduleTheme {
        WifiScreenView {}
    }
}