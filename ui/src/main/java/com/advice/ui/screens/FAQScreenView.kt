package com.advice.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.advice.schedule.models.firebase.FirebaseFAQ
import com.advice.ui.views.QuestionView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreenView(faqs: List<FirebaseFAQ>, onBackPressed: () -> Unit) {
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
fun FAQScreenContent(faqs: List<FirebaseFAQ>, modifier: Modifier = Modifier) {
    LazyColumn(modifier) {
        items(faqs) {
            QuestionView(it.question, it.answer)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FAQScreenViewPreview() {
    MaterialTheme {
        val faqs = listOf(FirebaseFAQ(-1, "DEFCON28", "Cost?", "$300"))
        FAQScreenView(faqs) {}
    }
}