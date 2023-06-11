package com.advice.organizations.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.advice.core.local.Organization

@Composable
internal fun OrganizationsScreenContent(
    organizations: List<Organization>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        items(organizations.chunked(2)) {
            OrganizationRow(it)
        }
    }
}