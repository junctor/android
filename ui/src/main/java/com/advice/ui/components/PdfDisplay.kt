package com.advice.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.barteksc.pdfviewer.util.FitPolicy
import timber.log.Timber
import java.io.File


@Composable
internal fun PdfDisplay(file: File, modifier: Modifier) {
    AndroidView(factory = { context ->
        com.github.barteksc.pdfviewer.PDFView(context, null)
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
