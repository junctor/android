package com.advice.schedule.database.repository

import com.advice.schedule.database.datasource.BookmarksDataSource
import com.advice.schedule.database.datasource.EventsDataSource
import com.advice.schedule.database.datasource.TagsDataSource
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.local.Event
import kotlinx.coroutines.flow.combine
import timber.log.Timber

class ScheduleRepository(
    private val eventsDataSource: EventsDataSource,
    private val bookmarksDataSource: BookmarksDataSource,
    private val tagsDataSource: TagsDataSource,
) {

    val list = combine(eventsDataSource.get(), bookmarksDataSource.get(), tagsDataSource.get()) { events, bookmarks, tags ->
        Timber.e("repo events: ${events.size}")
        val filter = tags.flatMap { it.tags }.filter { it.isSelected }

        val sortedEvents = events.sortedBy { it.start }

        // marking bookmarked items
        for (bookmark in bookmarks) {
            sortedEvents.find { it.id.toString() == bookmark.id }?.isBookmarked = bookmark.value
        }
        if(filter.isEmpty()) {
            return@combine sortedEvents
        }

        filter(sortedEvents, filter)
    }

    private fun filter(
        events: List<Event>,
        filter: List<FirebaseTag>
    ): List<Event> {
        val ids = filter.map { it.id }
        return events.filter { it.types.any { it.id in ids } }
    }

    fun bookmark(event: Event) {
        eventsDataSource.bookmark(event)
    }

}