package com.advice.organizations.ui

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
import com.advice.ui.views.EmptyView
import com.advice.ui.views.SearchableTopAppBar
import com.shortstack.hackertracker.R

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