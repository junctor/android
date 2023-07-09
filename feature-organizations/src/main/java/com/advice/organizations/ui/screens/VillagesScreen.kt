package com.advice.organizations.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.advice.core.local.Organization
import com.advice.organizations.ui.components.OrganizationsScreenContent
import com.advice.ui.components.EmptyView
import com.advice.ui.components.SearchableTopAppBar
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VillagesScreen(organizations: List<Organization>, onBackPressed: () -> Unit) {
    Scaffold(topBar = {
        SearchableTopAppBar(title = { Text("Villages") }, navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(painterResource(R.drawable.arrow_back), null)
            }
        }) { query ->

        }
    }) {
        if (organizations.isNotEmpty()) {
            OrganizationsScreenContent(organizations, modifier = Modifier.padding(it))
        } else {
            EmptyView("Villages not found")
        }
    }
}

@LightDarkPreview()
@Composable
private fun OrganizationsScreenViewPreview() {
    ScheduleTheme {
        VillagesScreen(
            organizations = listOf(
                Organization(
                    id = -1,
                    name = "360 Unicorn Team",
                    description = "360 Unicorn Team consists of a large team of self talk hackers that are really good at like, hacking and stuff.",
                    locations = emptyList(),
                    links = emptyList(),
                    media = emptyList(),
                    tags = emptyList(),
                ),
                Organization(
                    id = -1,
                    name = "EFF",
                    description = "360 Unicorn Team consists of a large team of self talk hackers that are really good at like, hacking and stuff.",
                    locations = emptyList(),
                    links = emptyList(),
                    media = emptyList(),
                    tags = emptyList(),
                )
            )
        ) {

        }
    }
}