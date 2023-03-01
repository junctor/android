package com.advice.data.datasource

import com.advice.core.local.Bookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkedElementDataSource {

    fun get(): Flow<List<Bookmark>>

    suspend fun clear()

    suspend fun bookmark(id: Long, isBookmarked: Boolean)
}