@file:OptIn(ExperimentalMaterial3Api::class)

package com.advice.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.advice.core.local.MapFile
import com.advice.ui.components.EmptyView
import com.advice.ui.theme.ScheduleTheme
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.baseline_arrow_back_ios_new_24), null)
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
        Box {
            if (maps.isEmpty()) {
                EmptyView("maps not found")
            } else {
                var file by remember { mutableStateOf(maps.first().name) }

                val temp = maps.find { it.name == file }!!
                MapsScreenContent(temp.file!!, Modifier.padding(it))

                MyScreen(
                    state,
                    maps.map { it.name },
                    Modifier.align(Alignment.BottomCenter), onMapChanged = {
                        file = it
                        coroutineScope.launch {
                            state.hide()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun MapsScreenContent(file: File, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        PDFView(
            file,
            Modifier
                .fillMaxSize()
        )
    }
}

@Composable
private fun PDFView(file: File, modifier: Modifier) {
    AndroidView(factory = { context ->
        PDFView(context, null)
    },
        modifier = modifier,
        update = { view ->
            Timber.d("loading: $file")
            view.fromFile(file)
                .pageFitPolicy(FitPolicy.HEIGHT)
                .onLoad { pages ->

                }
                .load()
        })
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MyScreen(
    state: ModalBottomSheetState,
    files: List<String>,
    modifier: Modifier = Modifier,
    onMapChanged: (String) -> Unit = {},
) {
    ModalBottomSheetLayout(
        sheetContent = {
            Box(modifier = Modifier.navigationBarsPadding()) {
                MySheetContent(files, onMapChanged)
            }
        },
        sheetState = state,
//        scrimColor = Color.Black.copy(alpha = 0.32f),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
    ) {
    }
}

@Composable
fun MySheetContent(
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
            Text(it, modifier = Modifier
                .clickable {
                    onMapChanged(it)
                }
                .fillMaxWidth()
                .padding(16.dp)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun MapsScreenViewPreview() {
    ScheduleTheme {
        MapsScreen(listOf(MapFile("Map", File("/")))) {

        }
    }
}