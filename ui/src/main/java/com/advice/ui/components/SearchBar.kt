package com.advice.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

/**
 * State-hoisted search field. Filtering/search is driven by [onQuery] as the user types;
 * the IME Search action only dismisses the keyboard.
 */
@Composable
fun SearchBar(
    query: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    onQuery: (String) -> Unit,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = onQuery,
        modifier = modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(Icons.Default.Search, null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQuery("") }) {
                    Icon(Icons.Default.Close, null)
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
            },
        ),
        placeholder = { Text(placeholder) },
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
    )
}

@PreviewLightDark
@Composable
private fun SearchBarPreview() {
    ScheduleTheme {
        Column(
            Modifier.padding(4.dp),
        ) {
            SearchBar("hello world", "Search", onQuery = {})
        }
    }
}
