package com.advice.organizations.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.advice.core.local.Organization
import com.advice.core.local.OrganizationLink
import com.advice.core.local.OrganizationMedia
import com.advice.ui.components.BackButton
import com.advice.ui.components.ClickableUrl
import com.advice.ui.components.ImageGallery
import com.advice.ui.components.NoDetailsView
import com.advice.ui.components.Paragraph
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.screens.ErrorScreen
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape

sealed class OrganizationScreenState {
    data object Loading : OrganizationScreenState()
    data class Success(val organization: Organization) : OrganizationScreenState()
    data object Error : OrganizationScreenState()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationScreen(
    state: OrganizationScreenState,
    onBackPressed: () -> Unit,
    onLinkClicked: (String) -> Unit,
    onScheduleClicked: (Long, String) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (state is OrganizationScreenState.Success) {
                        Text(text = state.organization.name)
                    }
                },
                navigationIcon = {
                    BackButton(onBackPressed)
                },
            )
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (state) {
                OrganizationScreenState.Error -> {
                    ErrorScreen {
                        onBackPressed()
                    }
                }

                OrganizationScreenState.Loading -> {
                    ProgressSpinner()
                }

                is OrganizationScreenState.Success -> {
                    Content(state.organization, onLinkClicked, onScheduleClicked)
                }
            }
        }
    }
}

@Composable
private fun Content(
    organization: Organization,
    onLinkClicked: (String) -> Unit,
    onScheduleClicked: (Long, String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(bottom = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (organization.media.isNotEmpty()) {
            ImageGallery(organization.media.map { it.url }, aspectRatio = 1.333f)
        }

        val tag = organization.tag
        if (tag != null) {
            Box(
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = { onScheduleClicked(tag, organization.name) },
                    Modifier
                        .fillMaxWidth()
                        .align(
                            Alignment.CenterEnd,
                        ),
                    shape = roundedCornerShape,
                ) {
                    Text("Show Schedule")
                }
            }
        }

        val description = organization.description
        if (description?.isNotBlank() == true) {
            Paragraph(description)
        } else {
            NoDetailsView()
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
            null,
            locations = emptyList(),
            links = listOf(
                OrganizationLink("Website", "website", "https://www.google.com"),
                OrganizationLink("Website", "website", "https://www.google.com"),
            ),
            media = listOf(
                OrganizationMedia(1, "https://picsum.photos/200/300")
            ),
            tag = 1,
            tags = listOf(1),
        )
        OrganizationScreen(
            state = OrganizationScreenState.Success(organization),
            onBackPressed = {},
            onLinkClicked = {},
            onScheduleClicked = { _, _ ->
            },
        )
    }
}
