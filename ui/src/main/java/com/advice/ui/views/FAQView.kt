package com.advice.ui.views

import android.graphics.drawable.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun QuestionView(question: String, answer: String) {
    var isExpanded by rememberSaveable {
        mutableStateOf(value = false)
    }

    Column {
        Row(
            Modifier
                .clickable {
                    isExpanded = !isExpanded
                }
                .padding(horizontal = 16.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = question, modifier = Modifier.weight(1f))
            IconButton(onClick = {
                isExpanded = !isExpanded
            }) {
                val rotation = if (isExpanded) 180f else 0f
                Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.rotate(rotation))
            }
        }
        if (isExpanded) {
            AnswerView(answer)
        }
    }
}

@Composable
fun AnswerView(answer: String) {
    Text(answer, modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true)
@Composable
fun QuestionViewPreview() {
    MaterialTheme {
        QuestionView("How much does DEFCON cost?", "$300 USD, cash only.")
    }
}