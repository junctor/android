package com.advice.organizations.ui.screens

import androidx.compose.foundation.layout.Box
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
import com.advice.core.local.Organization
import com.advice.organizations.ui.components.OrganizationsScreenContent
import com.advice.ui.R
import com.advice.ui.components.EmptyMessage
import com.advice.ui.components.ProgressSpinner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationsScreen(
    label: String,
    organizations: List<Organization>?,
    onBackPressed: () -> Unit,
    onOrganizationPressed: (Organization) -> Unit,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text(label) }, navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(painterResource(R.drawable.arrow_back), null)
            }
        })
    }) {
        Box(Modifier.padding(it)) {
            when {
                organizations == null -> {
                    ProgressSpinner()
                }

                organizations.isEmpty() -> {
                    EmptyMessage("$label not found")
                }

                else -> {
                    OrganizationsScreenContent(organizations, onOrganizationPressed)
                }
            }
        }
    }
}
