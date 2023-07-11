package com.advice.documents.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.local.Document
import com.advice.ui.R
import com.advice.ui.components.Paragraph
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentScreen(
    document: Document,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(document.title) },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
                    }
                }
            )
        }) {
        Column(
            Modifier
                .padding(it)
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Paragraph(document.description)
            Spacer(Modifier.height(64.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SupportScreenViewPreview() {
    ScheduleTheme {
        DocumentScreen(
            document = Document(
                -1L,
                "Code of Conduct",
                "If you need support, please call us 555-555-0000"
            ),
            onBackPressed = {}
        )
    }
}