package com.advice.firebase.data.sources

import com.advice.core.local.Event
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.BookmarkedEventsDataSource
import com.advice.data.sources.EventDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.extensions.toEvent
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.models.FirebaseEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseEventDataSource(
    private val firestore: FirebaseFirestore,
    private val tagsDataSource: TagsDataSource,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
) : EventDataSource {

    override suspend fun get(conference: String, id: Long): Event? {
        val snapshot = firestore.collection("conferences")
            .document(conference)
            .collection("events")
            .document(id.toString())
            .get()
            .await()

        val tags = tagsDataSource.get().first()
        val bookmarks = bookmarkedEventsDataSource.get().first()

        val event = snapshot.toObjectOrNull(FirebaseEvent::class.java)
            ?.toEvent(
                tags = tags,
                isBookmarked = bookmarks.any { it.id == id.toString() }
            )

        if (event == null) {
            Timber.e("Event with id $id not found")
        }
        return event
    }
}