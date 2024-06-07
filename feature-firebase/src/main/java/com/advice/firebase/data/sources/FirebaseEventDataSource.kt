package com.advice.firebase.data.sources

import com.advice.core.local.Event
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.EventDataSource
import com.advice.data.sources.LocationsDataSource
import com.advice.data.sources.SpeakersDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.extensions.toEvent
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.models.FirebaseEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseEventDataSource(
    private val firestore: FirebaseFirestore,
    private val tagsDataSource: TagsDataSource,
    private val speakersDataSource: SpeakersDataSource,
    private val locationsDataSource: LocationsDataSource,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
) : EventDataSource {

    override suspend fun get(conference: String, id: Long): Event? {
        val snapshot = firestore.collection("conferences")
            .document(conference)
            .collection("content")
            .document(id.toString())
            .get()
            .await()

        val tags = tagsDataSource.get().first()
        val speakers = speakersDataSource.get().first()
        val locations = locationsDataSource.get().first()
        val bookmarks = bookmarkedEventsDataSource.get().first()

        val event = snapshot.toObjectOrNull(FirebaseEvent::class.java)
            ?.toEvent(
                conference = conference,
                tags = tags,
                speakers = speakers,
                isBookmarked = bookmarks.any { it.id == id.toString() },
                locations = locations
            )

        if (event == null) {
            Timber.e("Event with id $id not found")
        }
        return event
    }
}
