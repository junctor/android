package com.advice.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.core.local.MapFile
import com.advice.ui.components.PdfDisplay
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.states.MapsScreenState
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.TemporarySystemBarScrims
import timber.log.Timber
import java.io.File

private val MapBarColor = Color.Black

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(
    state: MapsScreenState,
    onBackPress: () -> Unit,
    onMapChange: (String) -> Unit,
) {
    TemporarySystemBarScrims(MapBarColor)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    if (state is MapsScreenState.Success) {
                        Text(
                            state.file.name,
                            color = Color.White,
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onBackPress,
                        colors = IconButtonDefaults.iconButtonColors(),
                    ) {
                        Icon(
                            Icons.Default.Close,
                            "Close",
                            tint = Color.White,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MapBarColor,
                        scrolledContainerColor = Color.Unspecified,
                        navigationIconContentColor = Color.White,
                        titleContentColor = Color.Unspecified,
                        actionIconContentColor = Color.Unspecified,
                    ),
            )
        },
        bottomBar = {
            if (state is MapsScreenState.Success && state.maps.size > 1) {
                MapsBottomBar(
                    maps = state.maps.map { it.name },
                    selected = state.file.name,
                    onMapClick = onMapChange,
                )
            }
        },
    ) { innerPadding ->
        // Bars are opaque, so the map sits between them rather than under them.
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color.White),
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
                        Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
private fun MapsBottomBar(
    maps: List<String>,
    selected: String,
    onMapClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(MapBarColor)
                .navigationBarsPadding()
                .horizontalScroll(scrollState)
                .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        maps.forEach { name ->
            val isSelected = name == selected
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onMapClick(name) },
            ) {
                Text(
                    text = name.uppercase(),
                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                )
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    Color.Transparent
                                },
                            ),
                )
            }
        }
    }
}

@Composable
private fun EmptyScreen(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .padding(16.dp)
            .fillMaxSize(),
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
                state =
                    MapsScreenState.Success(
                        file = file,
                        maps =
                            listOf(
                                file,
                                MapFile("Floor 1", File("/")),
                                MapFile("Floor 2", File("/")),
                                MapFile("Village", File("/")),
                            ),
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
