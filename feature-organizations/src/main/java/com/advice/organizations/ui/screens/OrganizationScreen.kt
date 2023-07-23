package com.advice.organizations.ui.screens

import androidx.compose.foundation.layout.Column
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
import com.advice.core.local.Organization
import com.advice.ui.components.Paragraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizationScreen(
    organization: Organization,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(organization.name) }, navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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