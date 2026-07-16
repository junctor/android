package com.advice.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.FAQ
import com.advice.ui.components.BackButton
import com.advice.ui.components.EmptyMessage
import com.advice.ui.components.FreqAskedQuestion
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.SearchBar
import com.advice.ui.preview.FAQProvider
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(
    faqs: List<FAQ>?,
    onBackPress: () -> Unit,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("FAQ") },
            navigationIcon = {
                BackButton(onBackPress)
            },
        )
    }) {
        Box(Modifier.padding(it)) {
            when {
                faqs == null -> {
                    ProgressSpinner()
                }

                faqs.isEmpty() -> {
                    EmptyMessage(message = "FAQ not found")
                }

                else -> {
                    FAQScreenContent(faqs)
                }
            }
        }
    }
}

@Composable
private fun FAQScreenContent(
    faqs: List<FAQ>,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(faqs, query) {
        filterFaqs(faqs, query)
    }

    Column(modifier) {
        LazyColumn {
            item {
                Box(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    SearchBar(
                        query = query,
                        placeholder = "Search frequently asked questions",
                        onQuery = { query = it },
                    )
                }
            }
            if (filtered.isEmpty()) {
                item {
                    EmptyMessage(message = "No matching questions")
                }
            } else {
                items(filtered) {
                    FreqAskedQuestion(it.question, it.answer)
                }
            }
        }
    }
}

internal fun filterFaqs(faqs: List<FAQ>, query: String): List<FAQ> {
    if (query.length < 2) return faqs
    val needle = query.lowercase()
    return faqs.filter {
        it.question.lowercase().contains(needle) || it.answer.lowercase().contains(needle)
    }
}

@PreviewLightDark
@Composable
private fun FAQScreenViewPreview(
    @PreviewParameter(FAQProvider::class) faq: FAQ,
) {
    ScheduleTheme {
        FAQScreen(listOf(faq)) {}
    }
}
