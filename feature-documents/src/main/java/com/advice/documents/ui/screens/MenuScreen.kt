package com.advice.documents.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.advice.core.local.Menu
import com.advice.core.local.MenuItem
import com.advice.ui.R
import com.advice.ui.components.EmptyMessage
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.utils.MenuIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    label: String,
    menu: Menu?,
    onBackPressed: () -> Unit,
    onNavigationClick: (String) -> Unit,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text(label) }, navigationIcon = {
            IconButton(onClick = { onBackPressed() }) {
                Icon(painterResource(R.drawable.arrow_back), null)
            }
        })
    }) {
        Box(Modifier.padding(it)) {
            when {
                menu == null -> {
                    ProgressSpinner()
                }

                menu == null -> {
                    EmptyMessage("$label not found")
                }

                else -> {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        menu.items.forEach {
                            Surface(
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface.copy(0.15f)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 0.dp, vertical = 4.dp)
                            ) {
                                Row(Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = it.url != null) {
                                        val url = it.url
                                        if (url != null) {
                                            onNavigationClick(url)
                                        }
                                    }
                                    .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    MenuIcon(it.icon)
                                    Text(it.label)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
