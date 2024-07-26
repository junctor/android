package com.advice.ui.utils

import androidx.compose.ui.graphics.Color
import com.advice.core.local.Tag
import com.advice.ui.R
import timber.log.Timber

fun parseColor(color: String?): Color {
    if (color == null) {
        return Color.Blue
    }

    return try {
        Color(android.graphics.Color.parseColor(color))
    } catch (ex: Exception) {
        Timber.e("Error parsing color: $color")
        Color.Green
    }
}

fun createTag(
    label: String,
    color: String,
    isSelected: Boolean = false,
): Tag = Tag(-1, label, "", color, -1, isSelected)


fun String.toResource(): Int? {
    return when (this) {
        "balance" -> R.drawable.baseline_balance_24
        "calendar_month" -> R.drawable.baseline_calendar_month_24
        "description" -> R.drawable.outline_description_24
        "directions_bus" -> R.drawable.baseline_directions_bus_24
        "directions_walk" -> R.drawable.baseline_directions_walk_24
        "door_open" -> R.drawable.baseline_door_open_24
        "escalator_warning" -> R.drawable.baseline_escalator_warning_24
        "flag" -> R.drawable.baseline_flag_24
        "groups" -> R.drawable.baseline_groups_24
        "help" -> R.drawable.baseline_help_24
        "location_city" -> R.drawable.baseline_location_city_24
        "map" -> R.drawable.baseline_map_24
        "menu" -> R.drawable.baseline_menu_24
        "news" -> R.drawable.baseline_news_24
        "point_of_sale" -> R.drawable.baseline_point_of_sale_24
        "question_mark" -> R.drawable.baseline_question_mark_24
        "restaurant_menu" -> R.drawable.baseline_restaurant_menu_24
        "search" -> R.drawable.baseline_search_24
        "shopping_bag" -> R.drawable.baseline_shopping_bag_24
        "sprint" -> R.drawable.baseline_sprint_24
        "tv" -> R.drawable.baseline_tv_24
        "wallpaper_slideshow" -> R.drawable.baseline_wallpaper_slideshow_24
        "wifi" -> R.drawable.baseline_wifi_24
        else -> null
    }
}
