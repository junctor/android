package com.advice.schedule.data.repositories

import com.advice.core.local.Event
import com.advice.core.local.Tag
import com.advice.core.ui.ScheduleFilter
import com.advice.reminder.ReminderManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ScheduleRepository(
    private val eventsRepository: EventsRepository,
    private val tagsRepository: TagsRepository,
    private val reminderManager: ReminderManager,
) {

    suspend fun getEvent(conference: String, id: Long): Event? {
        return eventsRepository.get(conference, id)
    }

    fun getSchedule(filter: ScheduleFilter): Flow<List<Event>> {
        return combine(eventsRepository.events, tagsRepository.tags) { events, tags ->

            val sortedEvents = events.sortedBy { it.start }

            return@combine when (filter) {
                ScheduleFilter.Default -> {
                    val tags = tags.flatMap { it.tags }.filter { it.isSelected }
                    filter(sortedEvents, tags)
                }

                is ScheduleFilter.Location -> {
                    sortedEvents.filter { it.location.id.toString() == filter.id }
                }

                is ScheduleFilter.Tag -> {
                    TODO()
                }
            }
        }
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


    suspend fun bookmark(event: Event, isBookmarked: Boolean) {
        eventsRepository.bookmark(event)
        if (isBookmarked) {
            reminderManager.setReminder(event)
        } else {
            reminderManager.removeReminder(event)
        }
    }
}