package com.advice.schedule.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.shortstack.hackertracker.R

@Composable
fun DismissibleBottomAppBar(
    modifier: Modifier = Modifier,
    isShown: Boolean,
    content: @Composable RowScope.() -> Unit,
) {
    var offsetY by rememberSaveable { mutableStateOf(0f) }
    offsetY = if (isShown) 0f else with(LocalDensity.current) { 48.dp.toPx() }
    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
    )

    BottomAppBar(
        modifier = modifier
            .offset(y = animatedOffsetY.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
    ) {
        content()
    }
}

@LightDarkPreview
@Composable
fun DismissibleBottomAppBarPreview() {
    ScheduleTheme {
        DismissibleBottomAppBar(isShown = true) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(painterResource(id = R.drawable.skull), contentDescription = null)
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painterResource(id = R.drawable.ic_map_white_24dp),
                        contentDescription = null
                    )
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Info, contentDescription = null)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            }
        }
    }
}