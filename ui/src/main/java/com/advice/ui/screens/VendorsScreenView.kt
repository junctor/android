package com.advice.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.schedule.models.local.Vendor
import com.advice.ui.views.EmptyView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorsScreenView(vendors: List<Vendor>, onBackPressed: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Partners & Vendors") }, navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(Icons.Default.ArrowBack, null)
            }
        })
    }) {
        VendorsScreenContent(vendors, modifier = Modifier.padding(it))
    }
}

@Composable
fun VendorsScreenContent(vendors: List<Vendor>, modifier: Modifier = Modifier) {
    if (vendors.isNotEmpty()) {
        LazyColumn(modifier) {
            items(vendors) {
                VendorCard(it.name, it.summary, hasLink = it.link != null)
            }
        }
    } else {
        EmptyView("Vendors not found")
    }
}

@Composable
fun VendorCard(title: String, description: String, hasLink: Boolean) {
    Card(modifier = Modifier.padding(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End) {
            Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Text(description)
            if (hasLink) {
                OutlinedButton(onClick = { /*TODO*/ }) {
                    Text("Website")
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun VendorsScreenViewPreview() {
    MaterialTheme {
        VendorsScreenView(
            listOf(
                Vendor(
                    -1,
                    "360 Unicorn Team",
                    "360 Unicorn Team consists of a large team of self talk hackers that are really good at like, hacking and stuff.",
                    "google.com",
                    partner = false
                ),
                Vendor(
                    -1,
                    "360 Unicorn Team",
                    "360 Unicorn Team consists of a large team of self talk hackers that are really good at like, hacking and stuff.",
                    null,
                    partner = true
                )
            )
        ) {

        }
    }
}