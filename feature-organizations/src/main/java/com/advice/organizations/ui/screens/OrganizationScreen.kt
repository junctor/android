package com.advice.organizations.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.advice.core.local.Organization
import com.advice.core.local.OrganizationLink
import com.advice.ui.components.ClickableUrl
import com.advice.ui.components.Paragraph
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.screens.ImageScaffold
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationScreen(
    organization: Organization?,
    onBackPressed: () -> Unit,
    onLinkClicked: (String) -> Unit,
    onScheduleClicked: (Long, String) -> Unit,
) {
    ImageScaffold(
        url = organization?.media?.firstOrNull()?.url,
        imageModifier = Modifier.aspectRatio(1.333f),
        contentModifier = Modifier
            .verticalScroll(rememberScrollState()),
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed, colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Black.copy(0.40f),
                        )
                    ) {
                        Icon(
                            painterResource(id = com.advice.ui.R.drawable.arrow_back),
                            contentDescription = "Back",
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        }) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (organization != null) {
                Header(organization.name)

                val tag = organization.tag
                if (tag != null) {
                    Box(
                        Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = { onScheduleClicked(tag, organization.name) },
                            Modifier
                                .fillMaxWidth()
                                .align(
                                    Alignment.CenterEnd
                                )
                        ) {
                            Text("Show Schedule")
                        }
                    }
                }

                if (organization.description != null) {
                    Paragraph(organization.description ?: "")
                }
                if (organization.links.isNotEmpty()) {
                    Column {
                        for (link in organization.links) {
                            ClickableUrl(
                                label = link.label,
                                url = link.url,
                                onClick = {
                                    onLinkClicked(link.url)
                                },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Header(label: String) {
    Text(
        text = label,
        modifier = Modifier.padding(horizontal = 24.dp),
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Black,
    )
}

@PreviewLightDark
@Composable
private fun OrganizationScreenPreview() {
    ScheduleTheme {
        val organization = Organization(
            1,
            "Test Organization",
            "Hello World!",
            locations = emptyList(),
            links = listOf(
                OrganizationLink("Website", "website", "https://www.google.com"),
                OrganizationLink("Website", "website", "https://www.google.com"),
            ),
            media = listOf(

            ),
            tag = 1,
            tags = listOf(1),
        )
        OrganizationScreen(
            organization = organization,
            onBackPressed = {},
            onLinkClicked = {},
            onScheduleClicked = { _, _ ->

            },
        )
    }
}
