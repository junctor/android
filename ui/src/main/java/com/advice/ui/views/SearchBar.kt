package com.advice.ui.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import com.advice.ui.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme


@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
fun SearchBar(onQuery: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    keyboardController?.show()

    TextField(
        value = text, onValueChange = { newText ->
            onQuery(newText)
            text = newText
        }, modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            IconButton(onClick = {
                onQuery("")
                text = ""
                onDismiss()
            }) {
                Icon(Icons.Default.ArrowBack, null)
            }
        },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = {
                    onQuery("")
                    text = ""
                }) {
                    Icon(Icons.Default.Close, null)
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                // todo: search?
            }
        )
    )
}

@LightDarkPreview
@Composable
fun SearchBarPreview() {
    ScheduleTheme {
        SearchBar(onQuery = {}, onDismiss = {})
    }
}