package com.advice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.github.barteksc.pdfviewer.util.FitPolicy
import timber.log.Timber
import java.io.File

@Composable
internal fun PdfDisplay(
    file: File,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text("PDF: ${file.name}")
        }
        return
    }

    AndroidView(
        factory = { context ->
            com.github.barteksc.pdfviewer.PDFView(context, null)
        },
        modifier = modifier,
        update = { view ->
            if (file.exists() && file.isFile) {
                Timber.d("loading: $file")
                view.fromFile(file)
                    .pageFitPolicy(FitPolicy.HEIGHT)
                    .onLoad { _ -> }
                    .load()
            }
        },
    )
}
