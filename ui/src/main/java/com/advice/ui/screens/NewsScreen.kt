package com.advice.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.advice.core.local.NewsArticle
import com.advice.ui.R
import com.advice.ui.components.EmptyMessage
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.home.ArticleView
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(label: String?, news: List<NewsArticle>?, onBackPressed: () -> Unit) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(label ?: "News") },
            navigationIcon =
            {
                IconButton(onClick = { onBackPressed() }) {
                    Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
                }
            }
        )
    }) {
        Box(Modifier.padding(it)) {
            when {
                news == null -> {
                    ProgressSpinner()
                }

                news.isEmpty() -> {
                    EmptyMessage("News not found")
                }

                else -> {
                    LazyColumn {
                        items(news) {
                            ArticleView(text = it.text, date = it.date)
                        }
                        item {
                            Spacer(Modifier.height(64.dp))
                        }
                    }
                }
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun NewsScreenPreview() {
    ScheduleTheme {
        NewsScreen(
            label = "Announcements",
            news = emptyList()
        ) {}
    }
}
