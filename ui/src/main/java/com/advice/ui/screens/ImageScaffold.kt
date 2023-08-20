package com.advice.ui.screens

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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.isLight
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun ImageScaffold(
    url: String? = null,
    imageModifier: Modifier = Modifier,
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
    val systemUiController = rememberSystemUiController()
    val isLight = MaterialTheme.isLight()

    if (url != null) {
        DisposableEffect(Unit) {
            systemUiController.setSystemBarsColor(
                color = Color.Black.copy(0.40f),
            )

            onDispose {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = isLight,
                )
            }
        }
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
        val modifier = if (url != null) contentModifier else contentModifier.padding(it)
        Column(modifier) {
            if (url != null) {
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = imageModifier,
                )
            }
            content()
            if (url != null) {
                Spacer(Modifier.height(56.dp))
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun ImageScreenPreview() {
    ScheduleTheme {
        ImageScaffold(url = "", imageModifier = Modifier.aspectRatio(16f / 9f)) {
            Box {
                Text("hello world")
            }
        }
    }
}