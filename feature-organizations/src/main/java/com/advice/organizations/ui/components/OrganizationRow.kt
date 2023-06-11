package com.advice.organizations.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.advice.core.local.Organization


@Composable
internal fun OrganizationRow(organizations: List<Organization>) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        for (organization in organizations) {
            OrganizationCard(
                organization.name,
                organization.media.firstOrNull()?.url,
                modifier = Modifier.weight(1f)
            ) {
                // todo: open organization details
            }
        }
        if (organizations.size == 1) Box(modifier = Modifier.weight(1f))
    }
}
