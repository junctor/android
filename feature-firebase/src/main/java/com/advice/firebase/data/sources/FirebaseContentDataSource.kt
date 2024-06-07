package com.advice.firebase.data.sources

import com.advice.core.local.Event
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.ContentDataSource
import com.advice.data.sources.LocationsDataSource
import com.advice.data.sources.SpeakersDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.extensions.toEvents
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.models.FirebaseEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseContentDataSource(
    private val firestore: FirebaseFirestore,
    private val tagsDataSource: TagsDataSource,
    private val speakersDataSource: SpeakersDataSource,
    private val locationsDataSource: LocationsDataSource,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
) : ContentDataSource {

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
            ?.toEvents(
                conference = conference,
                tags = tags,
                speakers = speakers,
                bookmarkedEvents = bookmarks,
                locations = locations
            )

        if (event.isNullOrEmpty()) {
            Timber.e("Event with id $id not found")
            return null
        }

        // todo: handle multiple events with the same id
        return event.first()
    }
}
