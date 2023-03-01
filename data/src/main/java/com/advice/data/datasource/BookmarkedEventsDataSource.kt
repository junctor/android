package com.advice.data.datasource

import com.advice.core.firebase.FirebaseBookmark
import com.advice.data.DataSource


interface BookmarkedEventsDataSource : DataSource<FirebaseBookmark> {
    suspend fun bookmark(id: Long, isBookmarked: Boolean)
}

