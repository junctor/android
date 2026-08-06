package com.advice.schedule.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import kotlin.math.roundToInt

/** Material3 bottom app bar container height; used to fully dismiss off-screen. */
private val BottomAppBarHeight = 80.dp

@Composable
fun DismissibleBottomAppBar(
    modifier: Modifier = Modifier,
    isShown: Boolean,
    content: @Composable RowScope.() -> Unit,
) {
    val density = LocalDensity.current
    val navigationBarHeight = WindowInsets.navigationBars.getBottom(density)
    val hiddenOffset =
        with(density) { BottomAppBarHeight.toPx() } + navigationBarHeight
    var offsetY by rememberSaveable { mutableFloatStateOf(0f) }
    offsetY = if (isShown) 0f else hiddenOffset
    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        label = "bottomAppBarOffset",
    )

    BottomAppBar(
        modifier =
            modifier
                .navigationBarsPadding()
                .offset { IntOffset(0, animatedOffsetY.roundToInt()) },
        // Insets applied via navigationBarsPadding on the modifier so the bar surface
        // sits above the system nav; avoid double-padding inside the bar content.
        windowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        content()
    }
}

@PreviewLightDark
@Composable
private fun DismissibleBottomAppBarPreview() {
    ScheduleTheme {
        DismissibleBottomAppBar(isShown = true) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        painterResource(
                            id = com.advice.ui.R.drawable.baseline_event_note_24,
                        ),
                        contentDescription = "schedule",
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        painterResource(
                            id = com.advice.ui.R.drawable.baseline_map_24,
                        ),
                        contentDescription = "map",
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Info, contentDescription = "search")
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Settings, contentDescription = "settings")
                }
            }
        }
    }
}
