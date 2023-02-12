package com.advice.schedule.database.repository

import com.advice.schedule.database.datasource.BookmarksDataSource
import com.advice.schedule.database.datasource.EventsDataSource
import kotlinx.coroutines.flow.combine
import timber.log.Timber

class ScheduleRepository(
    private val eventsDataSource: EventsDataSource,
    private val bookmarksDataSource: BookmarksDataSource,
) {

    val list = combine(eventsDataSource.get(), bookmarksDataSource.get()) { events, bookmarks ->
        Timber.e("repo events: ${events.size}")
        // marking bookmarked items
        for (bookmark in bookmarks) {
            events.find { it.id.toString() == bookmark.id }?.isBookmarked = bookmark.value
        }
        events
    }

}