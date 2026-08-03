package com.advice.schedule.data.repositories

import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.core.local.FlowResult
import com.advice.core.local.Session
import com.advice.core.storage.ContentSyncStore
import com.advice.core.utils.NotificationHelper
import com.advice.data.sources.ContentDataSource
import com.advice.reminder.ReminderManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn

class ContentRepository(
    private val contentDataSource: ContentDataSource,
    private val reminderManager: ReminderManager,
    private val notificationHelper: NotificationHelper,
    private val storage: ContentSyncStore,
    applicationScope: CoroutineScope,
) {
    val content: SharedFlow<FlowResult<ConferenceContent>> =
        contentDataSource
            .get()
            .onEach { result ->
                if (result is FlowResult.Success) {
                    updateBookmarkedContent(result.value)
                }
            }.shareIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                replay = 1,
            )

    /**
     * On each sync update any reminders on Bookmarked Content that has been updated since last sync.
     */
    private fun updateBookmarkedContent(conferenceContent: ConferenceContent) {
        val updatedBookmarks =
            conferenceContent.content
                .filter { it -> it.isBookmarked || it.sessions.any { it.isBookmarked } }
                .also {
                    // Handling edge case for users that have bookmarked items before this update.
                    it.forEach { content ->
                        if (storage.getContentUpdatedTimestamp(content.id) == 0L) {
                            storage.setContentUpdatedTimestamp(content.id, content.updated.toEpochMilli())
                        }
                    }
                }.filter { storage.getContentUpdatedTimestamp(it.id) < it.updated.toEpochMilli() }
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

    suspend fun bookmark(
        content: Content,
        session: Session,
    ) {
        contentDataSource.bookmark(session)
        storage.setContentUpdatedTimestamp(content.id, content.updated.toEpochMilli())
    }

    suspend fun isBookmarked(content: Content): Boolean = contentDataSource.isBookmarked(content)

    suspend fun isBookmarked(session: Session): Boolean = contentDataSource.isBookmarked(session)
}
