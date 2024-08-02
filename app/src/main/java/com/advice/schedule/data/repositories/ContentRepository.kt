package com.advice.schedule.data.repositories

import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.core.local.Session
import com.advice.core.utils.NotificationHelper
import com.advice.core.utils.Storage
import com.advice.data.sources.ContentDataSource
import com.advice.reminder.ReminderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn

class ContentRepository(
    private val contentDataSource: ContentDataSource,
    private val reminderManager: ReminderManager,
    private val notificationHelper: NotificationHelper,
    private val storage: Storage,
) {
    val content: Flow<ConferenceContent> = contentDataSource
        .get()
        .onEach {
            updateBookmarkedContent(it)
        }
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            replay = 1,
        )

    /**
     * On each sync update any reminders on Bookmarked Content that has been updated since last sync.
     */
    private fun updateBookmarkedContent(conferenceContent: ConferenceContent) {
        val updatedBookmarks = conferenceContent.content
            .filter { it.isBookmarked || it.sessions.any { it.isBookmarked } }
            .also {
                // Handling edge case for users that have bookmarked items before this update.
                it.forEach {
                    if (storage.getContentUpdatedTimestamp(it.id) == 0L) {
                        storage.setContentUpdatedTimestamp(it.id, it.updated.toEpochMilli())
                    }
                }
            }
            .filter { storage.getContentUpdatedTimestamp(it.id) < it.updated.toEpochMilli() }
        for (bookmark in updatedBookmarks) {
            val sessions = bookmark.sessions.filter { it.isBookmarked }

            for (session in sessions) {
                reminderManager.updateReminders(bookmark, session)
                notificationHelper.notifySessionUpdated(Event(bookmark, session))
            }
            storage.setContentUpdatedTimestamp(bookmark.id, bookmark.updated.toEpochMilli())
        }
    }

    suspend fun getContent(
        conference: String,
        contentId: Long,
    ): Content? = contentDataSource.getContent(conference, contentId)

    suspend fun bookmark(content: Content) {
        contentDataSource.bookmark(content)
    }

    suspend fun bookmark(content: Content, session: Session) {
        contentDataSource.bookmark(session)
        storage.setContentUpdatedTimestamp(content.id, content.updated.toEpochMilli())
    }

    suspend fun isBookmarked(content: Content): Boolean {
        return contentDataSource.isBookmarked(content)
    }

    suspend fun isBookmarked(session: Session): Boolean {
        return contentDataSource.isBookmarked(session)
    }
}
