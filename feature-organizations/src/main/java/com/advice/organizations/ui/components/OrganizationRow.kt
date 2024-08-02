package com.advice.organizations.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.advice.core.local.Organization

@Composable
fun OrganizationRow(
    organizations: List<Organization>,
    onOrganizationPressed: (Organization) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth(),
    ) {
        for (organization in organizations) {
            OrganizationCard(
                organization.name,
                organization.media.firstOrNull()?.url,
                modifier = Modifier.weight(1f)
            ) {
                onOrganizationPressed(organization)
            }
        }
        if (organizations.size == 1) Box(modifier = Modifier.weight(1f))
    }
}
