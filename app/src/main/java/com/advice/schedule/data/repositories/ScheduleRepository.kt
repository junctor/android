package com.advice.schedule.data.repositories

import com.advice.core.local.Event
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.ui.ScheduleFilter
import com.advice.reminder.ReminderManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ScheduleRepository(
    private val contentRepository: ContentRepository,
    private val tagsRepository: TagsRepository,
    private val reminderManager: ReminderManager,
) {
    suspend fun getEvent(
        conference: String,
        id: Long,
    ): Event? = contentRepository.get(conference, id)

    fun getSchedule(filter: ScheduleFilter): Flow<List<Event>> {
        return combine(contentRepository.content, tagsRepository.tags) { content, tags ->

            val sortedEvents = content.events.sortedBy { it.session.start }

            return@combine when (filter) {
                ScheduleFilter.Default -> {
                    val selected = tags.filter { it.tags.any { it.isSelected } }
                    filter(sortedEvents, selected)
                }

                is ScheduleFilter.Location -> {
                    sortedEvents.filter {
                        it.session.location.id
                            .toString() == filter.id
                    }
                }

                is ScheduleFilter.Tag -> {
                    sortedEvents.filter { it.types.any { it.id.toString() == filter.id } }
                }

                is ScheduleFilter.Tags -> {
                    // Any content that have any of the selected tags
                    sortedEvents.filter { it.types.any { it.id.toString() in filter.ids } }
                }
            }
        }
    }

    private fun filter(
        events: List<Event>,
        filter: List<TagType>,
    ): List<Event> {
        if (filter.isEmpty()) {
            if (Tag.bookmark.isSelected) {
                return events.filter { it.isBookmarked }
            }
            return events
        }

        val groups = filter.map {
            it.tags.filter { it.isSelected }.map { it.id }
        }

        return events
            .filter {
                groups.all { ids ->
                    it.types.any { it.id in ids }
                }
            }
    }

    suspend fun bookmark(
        event: Event,
        isBookmarked: Boolean,
    ) {
        contentRepository.bookmark(event)
        if (isBookmarked) {
            reminderManager.setReminder(event)
        } else {
            reminderManager.removeReminder(event)
        }
    }
}
