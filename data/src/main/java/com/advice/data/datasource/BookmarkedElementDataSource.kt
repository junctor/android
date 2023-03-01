package com.advice.data.datasource

import com.advice.core.firebase.FirebaseBookmark
import kotlinx.coroutines.flow.Flow

interface BookmarkedElementDataSource {

    fun get(): Flow<List<FirebaseBookmark>>

    suspend fun clear()

    suspend fun bookmark(id: Long, isBookmarked: Boolean)
}