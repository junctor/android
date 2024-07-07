package com.advice.schedule.data.repositories

import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.core.local.Session
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.ui.ScheduleFilter
import com.advice.reminder.ReminderManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import timber.log.Timber

class ScheduleRepository(
    private val eventRepository: EventRepository,
    private val tagsRepository: TagsRepository,
    private val reminderManager: ReminderManager,
) {

    fun getSchedule(filter: ScheduleFilter): Flow<List<Event>> {
        return combine(eventRepository.events, tagsRepository.tags) { content, tags ->

            val events: List<Event> = content.flatMap { content ->
                content.sessions.map { session ->
                    Event(content, session)
                }
            }

            val sortedEvents = events.sortedBy { it.session.start }

            return@combine when (filter) {
                ScheduleFilter.Default -> {
                    val selected = tags.filter { it.tags.any { it.isSelected } }
                    filter(sortedEvents, selected)
                }

                is ScheduleFilter.Location -> {
                    sortedEvents.filter {
                        it.session.location.id == filter.id
                    }
                }

                is ScheduleFilter.Tag -> {
                    sortedEvents.filter { it.types.any { it.id == filter.id } }
                }

                is ScheduleFilter.Tags -> {
                    // Any content that have any of the selected tags
                    sortedEvents.filter { it.types.any { it.id in (filter.ids ?: emptyList()) } }
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
                return events.filter { it.session.isBookmarked }
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
        content: Content,
        session: Session?,
        isBookmarked: Boolean,
    ) {
        if (session != null) {
            bookmarkSession(content, session, isBookmarked)
        } else {
            bookmarkContent(content, isBookmarked)
        }
    }

    /**
     * Bookmark a content and all its sessions
     */
    private suspend fun bookmarkContent(
        content: Content,
        isBookmarked: Boolean
    ) {
        // Bookmarking content that has sessions
        if (content.sessions.isNotEmpty()) {
            val all = content.sessions.all { eventRepository.isBookmarked(it) }
            val any = content.sessions.any { eventRepository.isBookmarked(it) }

            when {
                !isBookmarked && all -> {
                    Timber.d("All sessions are bookmarked - unbookmarking all")
                    // All sessions are bookmarked - unbookmark them all
                    content.sessions.forEach {
                        eventRepository.bookmark(it)
                        reminderManager.removeReminder(content, it)
                    }
                }

                isBookmarked && !any -> {
                    Timber.d("No sessions are bookmarked - bookmarking all")
                    // No sessions are bookmarked - bookmark them all
                    content.sessions.forEach {
                        eventRepository.bookmark(it)
                        reminderManager.setReminder(content, it)
                    }
                }
            }
        }

        eventRepository.bookmark(content)
    }

    private suspend fun bookmarkSession(
        content: Content,
        session: Session,
        isBookmarked: Boolean,
    ) {
        val contentBookmarked = eventRepository.isBookmarked(content)

        eventRepository.bookmark(session)
        if (isBookmarked) {
            reminderManager.setReminder(content, session)
        } else {
            reminderManager.removeReminder(content, session)
        }

        val all = content.sessions.all { eventRepository.isBookmarked(it) }
        val none = content.sessions.none { eventRepository.isBookmarked(it) }

        if (all && !contentBookmarked) {
            Timber.d("All sessions are bookmarked - bookmarking Content")
            // All sessions are now bookmarked - bookmark Content as well.
            eventRepository.bookmark(content)
        } else if (none && contentBookmarked) {
            Timber.d("No sessions are bookmarked - unbookmarking Content")
            // No sessions are bookmarked - unbookmark Content as well.
            eventRepository.bookmark(content)
        }
    }
}
