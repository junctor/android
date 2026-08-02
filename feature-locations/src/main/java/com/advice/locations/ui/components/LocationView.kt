package com.advice.locations.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Location
import com.advice.core.local.LocationStatus
import com.advice.locations.R
import com.advice.locations.ui.preview.LocationProvider
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun Location(
    label: String,
    description: String?,
    status: LocationStatus,
    hasChildren: Boolean,
    isExpanded: Boolean,
    depth: Int,
    onScheduleClicked: () -> Unit,
) {
    val colour =
        when (status) {
            LocationStatus.Closed -> Color.Red
            LocationStatus.Mixed -> Color.Yellow
            LocationStatus.Open -> Color.Green
            LocationStatus.Unknown -> Color.Gray
        }
    val statusLabel =
        when (status) {
            LocationStatus.Closed -> stringResource(R.string.cd_location_status_closed)
            LocationStatus.Mixed -> stringResource(R.string.cd_location_status_mixed)
            LocationStatus.Open -> stringResource(R.string.cd_location_status_open)
            LocationStatus.Unknown -> stringResource(R.string.cd_location_status_unknown)
        }
    val expandState =
        when {
            !hasChildren -> null
            isExpanded -> stringResource(com.advice.ui.R.string.cd_expanded)
            else -> stringResource(com.advice.ui.R.string.cd_collapsed)
        }
    val spokenLabel =
        buildString {
            append(label)
            if (description != null) {
                append(", ")
                append(description)
            }
            append(", ")
            append(statusLabel)
        }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    contentDescription = spokenLabel
                    if (expandState != null) {
                        stateDescription = expandState
                    }
                }.clickable {
                    onScheduleClicked()
                }.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Spacer(modifier = Modifier.width((16 * depth).dp))
        Box(
            modifier =
                Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(colour),
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(label)
            if (description != null) {
                Text(description)
            }
        }

        if (hasChildren) {
            val rotation = if (isExpanded) 180f else 0f
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier =
                    Modifier
                        .padding(12.dp)
                        .rotate(rotation),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun LocationPreview(
    @PreviewParameter(LocationProvider::class) location: Location,
) {
    ScheduleTheme {
        Location(
            location.name,
            location.shortName,
            LocationStatus.Open,
            hasChildren = true,
            isExpanded = true,
            location.depth,
        ) {}
    }
}
