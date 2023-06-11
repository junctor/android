package com.advice.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.advice.core.local.MapFile
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.components.EmptyView
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreenView(maps: List<MapFile>, onBackPressed: () -> Unit) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("Maps") },
            navigationIcon = {
                IconButton(onClick = onBackPressed) {
                    Icon(Icons.Default.ArrowBack, null)
                }
            },
        )
    },
        bottomBar = {
            BottomAppBar(
                actions = {
                    maps.map {
                        Button(onClick = { /*TODO*/ }) {
                            Text(it.name)
                        }
                    }
                },
                containerColor = Color.Transparent
            )
        }) {
        MapsScreenContent(maps.mapNotNull { it.file }, Modifier.padding(it))
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MapsScreenContent(files: List<File>, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        if (files.isEmpty()) {
            EmptyView("maps not found")
        } else {
            HorizontalPager(
                files.size,
            ) {
                val file = files[it]
                PDFView(file, Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun PDFView(file: File, modifier: Modifier) {
    AndroidView(factory = { context ->
        PDFView(context, null)
    },
        modifier = modifier,
        update = { view ->
            println("loading: $file")
            view.fromFile(file)
                .pageFitPolicy(FitPolicy.HEIGHT)
                .onLoad { pages ->

                }
                .load()
        })
}

@Preview(showBackground = true)
@Composable
fun MapsScreenViewPreview() {
    ScheduleTheme {
        MapsScreenView(listOf(MapFile("Map", File("/")))) {

        }
    }
}