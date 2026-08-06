package com.advice.ui.components.pdf

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
internal fun ZoomControls(
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onFit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors =
        IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    Column(
        modifier = modifier.semantics(mergeDescendants = false) {},
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FilledIconButton(
            onClick = onZoomIn,
            modifier =
                Modifier
                    .size(48.dp)
                    .semantics { contentDescription = "Zoom in" },
            shape = CircleShape,
            colors = colors,
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
        FilledIconButton(
            onClick = onZoomOut,
            modifier =
                Modifier
                    .size(48.dp)
                    .semantics { contentDescription = "Zoom out" },
            shape = CircleShape,
            colors = colors,
        ) {
            Icon(Icons.Default.Remove, contentDescription = null)
        }
        FilledIconButton(
            onClick = onFit,
            modifier =
                Modifier
                    .size(48.dp)
                    .semantics { contentDescription = "Fit map to screen" },
            shape = CircleShape,
            colors = colors,
        ) {
            Icon(Icons.Default.ZoomOutMap, contentDescription = null)
        }
    }
}
