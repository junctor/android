package com.advice.organizations.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.advice.core.local.Organization
import com.advice.organizations.R
import com.advice.ui.components.ActionView
import com.advice.ui.components.Paragraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationScreen(
    organization: Organization,
    onBackPressed: () -> Unit,
    onLinkClicked: (String) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(organization.name) }, navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(
                        painterResource(id = com.advice.ui.R.drawable.baseline_arrow_back_ios_new_24),
                        contentDescription = "Back"
                    )
                }
            })
        }
    ) {
        Column(
            Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            Paragraph(organization.description ?: "")
            if (organization.links.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                for (link in organization.links) {
                    ActionView(
                        label = link.label,
                        url = link.url,
                        onClick = {
                            onLinkClicked(link.url)
                        },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

// todo: add preview
//@LightDarkPreview
//@Composable
//private fun OrganizationScreenPreview() {
//    ScheduleTheme {
//        val organization = Organization(1, "Test Organization", "Hello World!")
//        OrganizationScreen(organization, onBackPressed = {
//            println("Back pressed"))
//        }
//    }
//}