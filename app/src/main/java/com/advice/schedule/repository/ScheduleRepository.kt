package com.advice.schedule.repository

import com.advice.core.local.Event
import com.advice.core.local.Tag
import com.advice.data.sources.EventsDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.reminder.ReminderManager
import kotlinx.coroutines.flow.combine

class ScheduleRepository(
    private val eventsDataSource: EventsDataSource,
    private val tagsDataSource: TagsDataSource,
    private val reminderManager: ReminderManager,
) {

    val list = combine(eventsDataSource.get(), tagsDataSource.get()) { events, tags ->
        val filter = tags.flatMap { it.tags }.filter { it.isSelected }

        val sortedEvents = events.sortedBy { it.start }

        if (filter.isEmpty()) {
            return@combine sortedEvents
        }

        filter(sortedEvents, filter)
    }

    private fun filter(
        events: List<Event>,
        filter: List<Tag>,
    ): List<Event> {
        val bookmarksOnly = filter.any { it.isBookmark && it.isSelected }
        val ids = filter.filter { !it.isBookmark }.map { it.id }
        return events
            .filter { !bookmarksOnly || it.isBookmarked }
            .filter { ids.isEmpty() || it.types.any { it.id in ids } }
    }

    suspend fun bookmark(event: Event) {
        eventsDataSource.bookmark(event)
        // todo: check if we're bookmarking or removing it.
        reminderManager.setReminder(event)
    }

}