package com.advice.ui.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.advice.core.local.Tag
import com.advice.ui.R
import timber.log.Timber

fun parseColor(color: String?): Color {
    if (color == null)
        return Color.Blue

    return try {
        Color(android.graphics.Color.parseColor(color))
    } catch (ex: Exception) {
        Color.Green
    }
}

fun createTag(label: String, color: String, isSelected: Boolean = false): Tag {
    return Tag(-1, label, "", color, -1, isSelected)
}


@Composable
fun MenuIcon(resource: String?) {
    when (resource) {
        "search" -> Icon(
            Icons.Default.Search,
            contentDescription = resource
        )

        "description" -> Icon(
            painterResource(id = R.drawable.outline_description_24),
            contentDescription = resource
        )

        "menu" -> Icon(
            Icons.Default.Menu,
            contentDescription = resource
        )

        "calendar_month" -> Icon(
            painterResource(id = R.drawable.baseline_calendar_month_24),
            contentDescription = resource
        )

        "escalator_warning" -> Icon(
            painterResource(id = R.drawable.baseline_escalator_warning_24),
            contentDescription = resource
        )

        "groups" -> Icon(
            painterResource(id = R.drawable.baseline_groups_24),
            contentDescription = resource
        )

        "map" -> Icon(
            painterResource(id = R.drawable.baseline_map_24),
            contentDescription = resource
        )

        "location_on" -> Icon(
            Icons.Default.LocationOn,
            contentDescription = resource
        )

        "sprint" -> Icon(
            painterResource(id = R.drawable.baseline_directions_run_24),
            contentDescription = resource
        )

        "location_city" -> Icon(
            painterResource(id = R.drawable.baseline_location_city_24),
            contentDescription = resource
        )

        "shopping_bag" -> Icon(
            painterResource(id = R.drawable.baseline_shopping_bag_24),
            contentDescription = resource
        )

        "news" -> Icon(
            painterResource(id = R.drawable.baseline_newspaper_24),
            contentDescription = resource
        )

        else -> {
            Timber.e("Unknown icon: $resource")
        }
    }
}
