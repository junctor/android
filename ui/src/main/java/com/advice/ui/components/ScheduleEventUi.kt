package com.advice.ui.components

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.advice.core.local.Event
import com.advice.core.local.Tag
import com.advice.core.utils.TimeUtil
import com.advice.ui.utils.parseColor

data class EventTagUi(
    val label: String,
    val color: Color,
)

data class ScheduleEventUi(
    val event: Event,
    val title: String,
    val time: String,
    val location: String,
    val isBookmarked: Boolean,
    val dashColor: Color?,
    val tags: List<EventTagUi>,
)

fun Tag.toEventTagUi(): EventTagUi =
    EventTagUi(
        label = label,
        color = parseColor(color),
    )

fun List<Tag>.toEventTagUi(): List<EventTagUi> = map { it.toEventTagUi() }

fun Event.toScheduleEventUi(context: Context): ScheduleEventUi {
    val tagUi = types.toEventTagUi()
    return ScheduleEventUi(
        event = this,
        title = title,
        time = TimeUtil.getTimeStamp(context, session).replace(" ", "\n"),
        location = session.location.name,
        isBookmarked = session.isBookmarked,
        dashColor = tagUi.firstOrNull()?.color,
        tags = tagUi,
    )
}

fun Map<String, List<Event>>.toScheduleEventUi(
    context: Context,
): Map<String, List<ScheduleEventUi>> =
    mapValues { (_, events) ->
        events.map { it.toScheduleEventUi(context) }
    }
