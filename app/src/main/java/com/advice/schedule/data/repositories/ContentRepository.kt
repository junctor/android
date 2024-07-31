package com.advice.schedule.data.repositories

import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.data.sources.ContentDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class ContentRepository(
    private val contentDataSource: ContentDataSource,
) {
    val content: Flow<ConferenceContent> = contentDataSource
        .get()
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            replay = 1,
        )


    suspend fun getContent(
        conference: String,
        contentId: Long,
    ): Content? = contentDataSource.getContent(conference, contentId)

    suspend fun bookmark(content: Content) {
        contentDataSource.bookmark(content)
    }
}
