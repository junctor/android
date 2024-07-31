package com.advice.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
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
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.List
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.core.local.MapFile
import com.advice.ui.components.PdfDisplay
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.states.MapsScreenState
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MapsScreen(
    state: MapsScreenState,
    onBackPress: () -> Unit,
    onMapChange: (String) -> Unit,
) {
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
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

    ModalBottomSheetLayout(
        sheetContent = {
            if (state is MapsScreenState.Success) {
                MapsBottomSheet(
                    files = state.maps.map { it.name },
                    onMapClick = {
                        coroutineScope.launch {
                            bottomSheetState.hide()
                        }
                        onMapChange(it)
                    },
                    modifier = Modifier.navigationBarsPadding(),
                )
            }
        },
        sheetState = bottomSheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        if (state is MapsScreenState.Success) {
                            Text(
                                state.file.name, color = Color.White,
                            )
                        }

                    },
                    actions = {
                        IconButton(
                            onClick = onBackPress,
                            colors =
                            IconButtonDefaults.iconButtonColors(),
                        ) {
                            Icon(
                                Icons.Default.Close,
                                "Close",
                                tint = Color.White,
                            )
                        }
                    },
                    colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.40f),
                        navigationIconContentColor = Color.Black,
                    ),
                )
            },
            floatingActionButton = {
                if (state is MapsScreenState.Success) {
                    if (state.maps.size > 1) {
                        FloatingActionButton(shape = CircleShape, onClick = {
                            coroutineScope.launch {
                                bottomSheetState.show()
                            }
                        }) {
                            Icon(
                                Icons.Default.List,
                                "List",
                                tint = Color.White,
                            )
                        }
                    }
                }
            },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                when (state) {
                    MapsScreenState.Loading -> {
                        ProgressSpinner()
                    }

                    is MapsScreenState.Error -> {
                        EmptyScreen(
                            state.message,
                        )
                    }

                    is MapsScreenState.Success -> {
                        val file = state.file
                        Timber.d("Showing file: ${file.file}")
                        PdfDisplay(
                            file.file,
                            Modifier
                                .padding(it)
                                .fillMaxSize(),
                        )
                    }
                }
            }

        }
    }
}

@Composable
private fun MapsBottomSheet(
    files: List<String>,
    onMapClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
        modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        files.forEach {
            Text(
                it,
                color = MaterialTheme.colorScheme.onSurface,
                modifier =
                Modifier
                    .clickable {
                        onMapClick(it)
                    }
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun EmptyScreen(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            message,
            color = MaterialTheme.colorScheme.inverseOnSurface,
            fontWeight = FontWeight.Black,
            fontSize = 28.sp,
            lineHeight = 32.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
        )

    }
}

@PreviewLightDark
@Composable
private fun MapsScreenPreview() {
    ScheduleTheme {
        Surface {
            val file = MapFile("Map", File("/"))
            MapsScreen(
                state = MapsScreenState.Success(
                    file = file,
                    maps = listOf(file)
                ),
                onMapChange = {},
                onBackPress = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun MapsScreenErrorPreview() {
    ScheduleTheme {
        Surface {
            MapsScreen(
                state = MapsScreenState.Error("No maps for DEF CON 32"),
                onMapChange = {},
                onBackPress = {},
            )
        }
    }
}
