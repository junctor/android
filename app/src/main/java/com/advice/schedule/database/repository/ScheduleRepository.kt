package com.advice.schedule.database.repository

import com.advice.schedule.database.datasource.EventsDataSource
import com.advice.schedule.database.datasource.TagsDataSource
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.local.Event
import kotlinx.coroutines.flow.combine

class ScheduleRepository(
    private val eventsDataSource: EventsDataSource,
    private val tagsDataSource: TagsDataSource,
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
        filter: List<FirebaseTag>
    ): List<Event> {
        val bookmarksOnly = filter.any { it.isBookmark && it.isSelected }
        val ids = filter.filter { !it.isBookmark }.map { it.id }
        return events
            .filter { !bookmarksOnly || it.isBookmarked }
            .filter { ids.isEmpty() || it.types.any { it.id in ids } }
    }

    suspend fun bookmark(event: Event) {
        eventsDataSource.bookmark(event)
    }

}