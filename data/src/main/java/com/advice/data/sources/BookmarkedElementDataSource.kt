package com.advice.data.sources

import com.advice.core.local.Bookmark
import com.advice.core.local.Content
import com.advice.core.local.Session
import com.advice.core.local.Tag
import kotlinx.coroutines.flow.Flow

/**
 * Storage for bookmarked elements. Two independent instances are registered in DI
 * (see [BookmarkDataSourceQualifiers]): filter selections vs event/session bookmarks.
 *
 * [clear] empties **this** instance only — never assume it clears both stores.
 */
interface BookmarkedElementDataSource {
    fun get(): Flow<List<Bookmark>>

    suspend fun bookmark(
        content: Content,
        isBookmarked: Boolean,
    )

    suspend fun bookmark(
        session: Session,
        isBookmarked: Boolean,
    )

    suspend fun bookmark(
        tag: Tag,
        isBookmarked: Boolean,
    )

    suspend fun isBookmarked(content: Content): Boolean

    suspend fun isBookmarked(session: Session): Boolean

    suspend fun isBookmarked(tag: Tag): Boolean

    /** Clears all entries in this store instance. */
    suspend fun clear()
}
