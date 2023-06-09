package com.advice.organizations.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.advice.core.local.Organization
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

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

@Composable
private fun OrganizationRow(organizations: List<Organization>) {
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