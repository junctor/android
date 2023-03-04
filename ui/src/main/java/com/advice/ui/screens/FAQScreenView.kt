package com.advice.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.advice.core.local.FAQ
import com.advice.ui.preview.FAQProvider
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.views.QuestionView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreenView(faqs: List<FAQ>, onBackPressed: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Frequently Asked Questions") }, navigationIcon =
        {
            IconButton(onClick = { onBackPressed() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        })
    }) {
        FAQScreenContent(faqs, modifier = Modifier.padding(it))
    }
}

@Composable
fun FAQScreenContent(faqs: List<FAQ>, modifier: Modifier = Modifier) {
    LazyColumn(modifier) {
        items(faqs) {
            QuestionView(it.question, it.answer)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FAQScreenViewPreview(
    @PreviewParameter(FAQProvider::class) faq: FAQ
) {
    ScheduleTheme {
        FAQScreenView(listOf(faq)) {}
    }
}