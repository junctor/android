package com.advice.schedule.database.repository

import com.advice.schedule.database.datasource.EventsDataSource
import com.advice.schedule.database.datasource.TagsDataSource
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.local.Event
import kotlinx.coroutines.flow.combine
import timber.log.Timber

class ScheduleRepository(
    private val eventsDataSource: EventsDataSource,
    private val tagsDataSource: TagsDataSource,
) {

    val list = combine(eventsDataSource.get(), tagsDataSource.get()) { events, tags ->
        Timber.e("repo events: ${events.size}")
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
        val ids = filter.map { it.id }
        return events.filter { it.types.any { it.id in ids } }
    }

    suspend fun bookmark(event: Event) {
        eventsDataSource.bookmark(event)
    }

}