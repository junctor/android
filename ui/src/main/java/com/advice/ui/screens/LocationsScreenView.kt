package com.advice.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.schedule.models.local.LocationContainer
import com.advice.schedule.models.local.LocationStatus
import com.advice.ui.views.SearchableTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsScreenView(containers: List<LocationContainer>, onBackPressed: () -> Unit) {
    Scaffold(topBar = {
        SearchableTopAppBar(
            title = { Text("Locations") },
            navigationIcon = {
                IconButton(onClick = { onBackPressed() }) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            }
        ) { query ->

        }
    }) {
        LocationsScreenContent(containers, modifier = Modifier.padding(it))
    }
}

@Composable
fun LocationsScreenContent(containers: List<LocationContainer>, modifier: Modifier) {
    LazyColumn(modifier) {
        items(containers) {
            LocationView(it.title, null, it.status, it.hasChildren)
        }
    }
}

@Composable
fun LocationView(label: String, description: String?, status: LocationStatus, hasChildren: Boolean) {
    val colour = when (status) {
        LocationStatus.Closed -> Color.Red
        LocationStatus.Mixed -> Color.Yellow
        LocationStatus.Open -> Color.Green
        LocationStatus.Unknown -> Color.Gray
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .clickable {
            // todo:
        }
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(colour)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(label)
            if (description != null) {
                Text(description)
            }
        }

        if (hasChildren) {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Default.KeyboardArrowDown, null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationViewPreview() {
    MaterialTheme {
        LocationView("Caesars Forum", "Closes in 30 minutes", LocationStatus.Open, hasChildren = true)
    }
}

@Preview(showBackground = true)
@Composable
fun LocationsScreenViewPreview() {
    MaterialTheme {
        LocationsScreenView(emptyList()) {

        }
    }
}