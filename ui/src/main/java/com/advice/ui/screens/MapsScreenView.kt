package com.advice.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.advice.core.local.MapFile
import com.github.barteksc.pdfviewer.PDFView
import com.github.barteksc.pdfviewer.util.FitPolicy
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreenView(maps: List<MapFile>, onBackPressed: () -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Maps") }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.ArrowBack, null)
            }
        })
    }) {
        MapsScreenContent(maps.mapNotNull { it.file }, Modifier.padding(it))
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MapsScreenContent(files: List<File>, modifier: Modifier = Modifier) {
    HorizontalPager(
        files.size,
        modifier.fillMaxSize()
    ) {
        val file = files[it]
        PDFView(file, Modifier.fillMaxSize())
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
    MaterialTheme {
        MapsScreenView(listOf(MapFile("Map", File("/")))) {

        }
    }
}