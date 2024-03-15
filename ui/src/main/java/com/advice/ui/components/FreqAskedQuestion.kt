package com.advice.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.FAQ
import com.advice.ui.preview.FAQProvider
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun FreqAskedQuestion(question: String, answer: String, expanded: Boolean = false) {
    var isExpanded by rememberSaveable {
        mutableStateOf(value = expanded)
    }

    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.15f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    ) {

        Column {
            Row(
                Modifier
                    .clickable {
                        isExpanded = !isExpanded
                    }
                    .padding(start = 16.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.labelLarge,
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = {
                    isExpanded = !isExpanded
                }) {
                    val rotation = if (isExpanded) 180f else 0f
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        null,
                        modifier = Modifier.rotate(rotation)
                    )
                }
            }
            AnimatedVisibility(isExpanded) {
                Answer(answer)
            }
        }
    }
}

@Composable
private fun Answer(answer: String) {
    Text(answer, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
}

@LightDarkPreview
@Composable
private fun FreqAskedQuestionPreview(@PreviewParameter(FAQProvider::class) faq: FAQ) {
    ScheduleTheme {
        Column {
            FreqAskedQuestion(faq.question, faq.answer)
            FreqAskedQuestion(faq.question, faq.answer, expanded = true)
        }
    }
}
