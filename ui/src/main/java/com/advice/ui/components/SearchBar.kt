package com.advice.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
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
        }, modifier = Modifier
            .systemBarsPadding()
            .fillMaxWidth()
            .height(64.dp),
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
            AnimatedVisibility(text.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
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
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.background,
            focusedIndicatorColor = Color.White
        )
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
fun SearchBar(modifier: Modifier = Modifier, onQuery: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    keyboardController?.show()

    OutlinedTextField(
        value = text,
        onValueChange = { newText ->
            onQuery(newText)
            text = newText
        },
        modifier = modifier
            .fillMaxWidth(),
        leadingIcon = {
            Icon(Icons.Default.Search, null)
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
        ),
        placeholder = { Text("Search frequently asked questions") },
        shape = RoundedCornerShape(8.dp)
    )
}

@LightDarkPreview
@Composable
fun SearchBarPreview() {
    ScheduleTheme {
        Column(
            Modifier
                .padding(4.dp)
        ) {
            SearchBar(onQuery = {}, onDismiss = {})
            Spacer(modifier = Modifier.height(4.dp))
            SearchBar(onQuery = {})
        }
    }
}