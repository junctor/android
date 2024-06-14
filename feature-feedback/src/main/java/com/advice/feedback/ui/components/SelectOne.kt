package com.advice.feedback.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SelectOneItem(
    caption: String,
    options: List<String>,
) {
    var choice by remember { mutableStateOf(options[0]) }

    Column {
        Text(caption)

        if (options.size > 3) {
            options.forEach {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = choice == it,
                        onClick = { choice = it },
                    )
                    Text(it)
                }
            }
        } else {
            Row(Modifier.fillMaxWidth()) {
                options.forEach {
                    Column(
                        Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        RadioButton(
                            selected = choice == it,
                            onClick = { choice = it },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Text(it, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}
