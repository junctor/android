package com.advice.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.advice.core.local.Vendor
import com.advice.ui.preview.LightDarkPreview
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
            items(vendors.chunked(2)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    for (vendor in it) {
                        VendorCard(
                            vendor.name,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (it.size == 1) Box(modifier = Modifier.weight(1f))
                }
            }
        }
    } else {
        EmptyView("Vendors not found")
    }
}

@Composable
fun VendorCard(
    title: String,
    modifier: Modifier = Modifier,
) {

    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column() {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painterResource(id = R.drawable.logo_eff),
                    null,
                    modifier = Modifier
                        .background(Color.White)
                        .aspectRatio(1.333f)
                    //.fillMaxWidth()

                )
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    title + "\n",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
//                Text(
//                    "description",
//                    style = MaterialTheme.typography.bodyMedium
//                )
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
                    partner = false,
                    image = "google.com"
                ),
                Vendor(
                    -1,
                    "EFF",
                    "360 Unicorn Team consists of a large team of self talk hackers that are really good at like, hacking and stuff.",
                    null,
                    partner = true
                )
            )
        ) {

        }
    }
}