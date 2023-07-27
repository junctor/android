package com.advice.organizations.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.core.local.Organization
import com.advice.core.local.OrganizationLink
import com.advice.ui.components.ClickableUrl
import com.advice.ui.components.Paragraph
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationScreen(
    organization: Organization,
    onBackPressed: () -> Unit,
    onLinkClicked: (String) -> Unit,
) {
    val systemUiController = rememberSystemUiController()

    val hasMedia = organization.media.isNotEmpty()

    if (hasMedia) {
        DisposableEffect(Unit) {
            systemUiController.setSystemBarsColor(
                color = Color.Black.copy(0.40f),
            )

            onDispose {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                )
            }
        }
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(text = organization.name)
            },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        painterResource(id = com.advice.ui.R.drawable.baseline_arrow_back_ios_new_24),
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
            Modifier
                .padding(it)
                .verticalScroll(rememberScrollState()),
        ) {
            if (organization.media.isNotEmpty()) {
                AsyncImage(
                    model = organization.media.first().url,
                    contentDescription = "image",
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                )
            }
            if (organization.description != null) {
                Paragraph(organization.description ?: "")
            }
            if (organization.links.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
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

@LightDarkPreview
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
            ),
            media = listOf(),
            tags = listOf(),
        )
        OrganizationScreen(
            organization = organization,
            onBackPressed = {},
            onLinkClicked = {},
        )
    }
}
