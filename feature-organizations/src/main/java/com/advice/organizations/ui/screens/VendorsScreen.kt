package com.advice.organizations.ui.screens

import androidx.compose.foundation.layout.Box
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
import com.advice.ui.R
import com.advice.ui.components.ProgressSpinner

@Composable
fun VendorsScreen(organizations: List<Organization>?, onBackPressed: () -> Unit) {
    Scaffold(topBar = {
        SearchableTopAppBar(title = { Text("Vendors") }, navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(painterResource(R.drawable.arrow_back), null)
            }
        }) { query ->

        }
    }) {
        Box(Modifier.padding(it)) {
            when {
                organizations == null -> {
                    ProgressSpinner()
                }

                organizations.isEmpty() -> {
                    EmptyView("Vendors not found")
                }

                else -> {
                    OrganizationsScreenContent(organizations)
                }
            }
        }
    }
}