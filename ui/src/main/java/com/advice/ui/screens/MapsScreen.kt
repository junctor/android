@file:OptIn(ExperimentalMaterial3Api::class)

package com.advice.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.local.MapFile
import com.advice.ui.components.EmptyView
import com.advice.ui.components.PdfDisplay
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shortstack.hackertracker.R
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MapsScreen(maps: List<MapFile>, onBackPressed: () -> Unit) {
    val state = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()

    val systemUiController = rememberSystemUiController()

    DisposableEffect(Unit) {
        systemUiController.setSystemBarsColor(
            color = Color.Black.copy(0.40f),
        )

        onDispose {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
            )
        }
    }

    Timber.e("maps: $maps")

    if (maps.isEmpty()) {
        EmptyScreen(onBackPressed)
        return
    }

    var file by remember { mutableStateOf(maps.first().name) }
    val temp = maps.find { it.name == file }!!

    ModalBottomSheetLayout(
        sheetContent = {
            MapsBottomSheet(
                files = maps.map { it.name },
                onMapChanged = {
                    file = it
                    coroutineScope.launch {
                        state.hide()
                    }
                },
                modifier = Modifier.navigationBarsPadding()
            )
        },
        sheetState = state,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                                null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        navigationIconContentColor = Color.Black,
                    ),
                )
            },
            floatingActionButton = {
                if (maps.isNotEmpty()) {
                    FloatingActionButton(shape = CircleShape, onClick = {
                        coroutineScope.launch {
                            state.show()
                        }
                    }) {
                        Icon(Icons.Default.List, null)
                    }
                }
            },
        ) {
            Timber.e("file: ${temp.file}")

            PdfDisplay(
                temp.file!!,
                Modifier
                    .padding(it)
                    .fillMaxSize()
            )
        }
    }
}

@Composable
private fun MapsBottomSheet(
    files: List<String>,
    onMapChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        files.forEach {
            Text(it,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clickable {
                        onMapChanged(it)
                    }
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EmptyScreen(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                            null
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = Color.Black,
                ),
            )
        },
    ) {
        EmptyView("maps not found", Modifier.padding(it))
    }
}

@Preview(showBackground = true)
@Composable
private fun MapsScreenPreview() {
    ScheduleTheme {
        MapsScreen(listOf(MapFile("Map", File("/")))) {

        }
    }
}