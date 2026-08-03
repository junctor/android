package com.advice.schedule.domain

import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.Session
import com.advice.reminder.ReminderManager
import com.advice.schedule.data.repositories.ContentRepository
import timber.log.Timber

/**
 * Single place for bookmark toggles and reminder set/remove/reschedule.
 */
class ContentBookmarkUseCase(
    private val contentRepository: ContentRepository,
    private val reminderManager: ReminderManager,
) {
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

    fun rescheduleBookmarkedReminders() {
        val result = contentRepository.content.replayCache.firstOrNull() ?: return
        val conferenceContent = result.toResultOrNull() ?: return
        rescheduleBookmarkedReminders(conferenceContent)
    }

    fun rescheduleBookmarkedReminders(conferenceContent: ConferenceContent) {
        for (item in conferenceContent.content) {
            val sessions = item.sessions.filter { it.isBookmarked }
            for (session in sessions) {
                reminderManager.updateReminders(item, session)
            }
        }
    }

    private suspend fun bookmarkContent(
        content: Content,
        isBookmarked: Boolean,
    ) {
        if (content.sessions.isNotEmpty()) {
            val all = content.sessions.all { contentRepository.isBookmarked(it) }
            val any = content.sessions.any { contentRepository.isBookmarked(it) }

            when {
                !isBookmarked && all -> {
                    Timber.d("All sessions are bookmarked - unbookmarking all")
                    content.sessions.forEach {
                        contentRepository.bookmark(content, it)
                        reminderManager.removeReminders(content, it)
                    }
                }

                isBookmarked && !any -> {
                    Timber.d("No sessions are bookmarked - bookmarking all")
                    content.sessions.forEach {
                        contentRepository.bookmark(content, it)
                        reminderManager.setReminders(content, it)
                    }
                }
            }
        }

        contentRepository.bookmark(content)
    }

    private suspend fun bookmarkSession(
        content: Content,
        session: Session,
        isBookmarked: Boolean,
    ) {
        val contentBookmarked = contentRepository.isBookmarked(content)

        contentRepository.bookmark(content, session)
        if (isBookmarked) {
            reminderManager.setReminders(content, session)
        } else {
            reminderManager.removeReminders(content, session)
        }

        val all = content.sessions.all { contentRepository.isBookmarked(it) }
        val none = content.sessions.none { contentRepository.isBookmarked(it) }

        if (all && !contentBookmarked) {
            Timber.d("All sessions are bookmarked - bookmarking Content")
            contentRepository.bookmark(content)
        } else if (none && contentBookmarked) {
            Timber.d("No sessions are bookmarked - unbookmarking Content")
            contentRepository.bookmark(content)
        }
    }
}
