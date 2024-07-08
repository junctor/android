package com.advice.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.advice.core.local.Content
import com.advice.ui.components.ContentRow
import com.advice.ui.states.ContentScreenState
import com.advice.ui.theme.roundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentListScreen(
    state: ContentScreenState,
    label: String?,
    onMenuClick: () -> Unit,
    onContentClick: (Content) -> Unit,
    onBookmark: (Content, Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(label ?: "Content")
                },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, "Menu")
                    }
                },
            )
        },
        modifier =
            Modifier
                .clip(roundedCornerShape),
    ) {
        ContentScreenContent(state, onContentClick, onBookmark, Modifier.padding(it))
    }
}

@Composable
fun ContentScreenContent(
    state: ContentScreenState,
    onContentClick: (Content) -> Unit,
    onBookmark: (Content, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        LazyColumn {
            items(state.content) { content ->
                ContentRow(
                    title = content.title,
                    tags = content.types,
                    isBookmarked = content.isBookmarked,
                    onContentPressed = {
                        onContentClick(content)
                    },
                    onBookmark = { isBookmarked ->
                        onBookmark(content, isBookmarked)
                    },
                )
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
