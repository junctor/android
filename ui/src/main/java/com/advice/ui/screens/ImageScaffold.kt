package com.advice.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.advice.ui.components.Image
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.TemporarySystemBarScrims

@Composable
fun ImageScaffold(
    modifier: Modifier = Modifier,
    url: String? = null,
    contentModifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = contentColorFor(containerColor),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable () -> Unit,
) {
    if (url != null) {
        TemporarySystemBarScrims(Color.Black.copy(alpha = 0.40f))
    }

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        containerColor = containerColor,
        contentColor = contentColor,
        contentWindowInsets = contentWindowInsets,
    ) {
        val cMod = if (url != null) contentModifier else contentModifier.padding(it)
        Column(cMod) {
            if (url != null) {
                Box(Modifier.background(Color.Black)) {
                    val request =
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(url)
                            .build()

                    Image(
                        request = request,
                        contentDescription = "background image",
                        contentScale = ContentScale.Fit,
                        modifier = modifier,
                    )
                }
            }
            content()
            if (url != null) {
                Spacer(Modifier.height(56.dp))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ImageScreenPreview() {
    ScheduleTheme {
        ImageScaffold(modifier = Modifier.aspectRatio(16f / 9f), url = "", content = {
            Box {
                Text("hello world")
            }
        })
    }
}
