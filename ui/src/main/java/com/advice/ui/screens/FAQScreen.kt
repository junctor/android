package com.advice.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.FAQ
import com.advice.ui.R
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
            navigationIcon =
                {
                    IconButton(onClick = { onBackPress() }) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
                    }
                },
        )
    }) {
        Box(Modifier.padding(it)) {
            when {
                faqs == null -> {
                    ProgressSpinner()
                }

                faqs.isEmpty() -> {
                    EmptyMessage("FAQ not found")
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
    Column(modifier) {
        LazyColumn {
            item {
                TopBar()
            }
            items(faqs) {
                FreqAskedQuestion(it.question, it.answer)
            }
        }
    }
}

@Composable
private fun TopBar() {
    Box(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        SearchBar("", "Search frequently asked questions") {
        }
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
