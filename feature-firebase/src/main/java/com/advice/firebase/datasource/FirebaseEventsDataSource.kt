package com.advice.firebase.datasource

import com.advice.data.UserSession
import com.advice.data.datasource.BookmarkedElementDataSource
import com.advice.data.datasource.EventsDataSource
import com.advice.data.datasource.TagsDataSource
import com.advice.firebase.models.FirebaseEvent
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toEvent
import com.advice.firebase.toObjectsOrEmpty
import com.advice.schedule.models.local.Event
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import timber.log.Timber

class FirebaseEventsDataSource(
    private val userSession: UserSession,
    private val tagsDataSource: TagsDataSource,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
    private val firestore: FirebaseFirestore
) : EventsDataSource {

    private fun getEvents(conference: String): Flow<List<FirebaseEvent>> {
        return firestore.collection("conferences")
            .document(conference)
            .collection("events")
            .snapshotFlow()
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseEvent::class.java)
                    .filter { (!it.hidden || userSession.isDeveloper) }
            }
    }

    override fun get(): Flow<List<Event>> {
        // todo: fix issue when switching between conferences (concurrency = 1?)
        return userSession.getConference().flatMapMerge { conference ->
            combine(getEvents(conference.code), tagsDataSource.get(), bookmarkedEventsDataSource.get()) { events, tags, bookmarks ->
                Timber.e("firebase events: ${events.size}, tags: ${tags.size}")
                val events = events.mapNotNull { it.toEvent(tags) }

                for (bookmark in bookmarks) {
                    events.find { it.id.toString() == bookmark.id }?.isBookmarked = bookmark.value
                }

                events.also {
                    Timber.e("events: ${it.size}")
                }
            }
        }
    }


    override suspend fun bookmark(event: Event) {
        bookmarkedEventsDataSource.bookmark(event.id, isBookmarked = !event.isBookmarked)
    }
}

