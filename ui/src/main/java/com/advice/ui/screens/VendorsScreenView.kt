package com.advice.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.advice.core.local.Vendor
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.HotPink
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.views.EmptyView
import com.advice.ui.views.SearchableTopAppBar
import com.shortstack.hackertracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorsScreenView(vendors: List<Vendor>, onBackPressed: () -> Unit) {
    Scaffold(topBar = {
        SearchableTopAppBar(title = { Text("Partners & Vendors") }, navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(painterResource(R.drawable.arrow_back), null)
            }
        }) { query ->

        }
    }) {
        VendorsScreenContent(vendors, modifier = Modifier.padding(it))
    }
}

@Composable
fun VendorsScreenContent(vendors: List<Vendor>, modifier: Modifier = Modifier) {
    if (vendors.isNotEmpty()) {
        LazyColumn(modifier) {
            items(vendors) {
                VendorCard(it.name, it.summary, it.partner, hasLink = it.link != null)
            }
        }
    } else {
        EmptyView("Vendors not found")
    }
}

@Composable
fun VendorCard(title: String, description: String?, isPartner: Boolean, hasLink: Boolean) {
    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.15f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.End) {
            Row() {
                Text(
                    title,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
                if (isPartner) {
                    Spacer(Modifier.width(16.dp))
                    Text("Partner".uppercase(), fontWeight = FontWeight.Bold, color = HotPink)
                }
            }
            Spacer(Modifier.height(16.dp))
            if (description != null) {
                Text(
                    description,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (hasLink) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { /*TODO*/ }) {
                    Text("Website")
                }
            }
        }
    }
}

@LightDarkPreview()
@Composable
fun VendorsScreenViewPreview() {
    ScheduleTheme {
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