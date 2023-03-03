package com.advice.ui.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.advice.ui.theme.ScheduleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchableTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    onSearchQuery: (String) -> Unit,
) {
    var isSearching by remember {
        mutableStateOf(false)
    }

    if (!isSearching) {
        CenterAlignedTopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = {
                IconButton(onClick = {
                    isSearching = true
                }) {
                    Icon(Icons.Default.Search, null)
                }

            }
        )
    } else {
        SearchBar(
            onQuery = {
                onSearchQuery(it)
            },
            onDismiss = {
                isSearching = false
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchableToolbarPreview() {
    ScheduleTheme {
        SearchableTopAppBar( title = { Text("Schedule")}) {

        }
    }
}