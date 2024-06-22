package com.advice.firebase.data.sources

import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.core.local.Location
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.ContentDataSource
import com.advice.data.sources.LocationsDataSource
import com.advice.data.sources.SpeakersDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.extensions.toContents
import com.advice.firebase.extensions.toEvents
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.models.FirebaseContent
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

    override suspend fun getContent(conference: String, id: Long): Content? {
        val tags = tagsDataSource.get().first()
        val speakers = speakersDataSource.get().first()
        val locations = locationsDataSource.get().first().flatten()
        val bookmarks = bookmarkedEventsDataSource.get().first()

        val content = getFirebaseContentOrNull(conference, id)
            ?.toContents(
                code = conference,
                tags = tags,
                speakers = speakers,
                bookmarkedEvents = bookmarks,
                locations = locations,
            )

        return content
    }

    override suspend fun getEvent(
        conference: String,
        id: Long,
    ): Event? {
        val tags = tagsDataSource.get().first()
        val speakers = speakersDataSource.get().first()
        val locations = locationsDataSource.get().first().flatten()
        val bookmarks = bookmarkedEventsDataSource.get().first()

        val event =
            getFirebaseContentOrNull(conference, id)
                ?.toEvents(
                    code = conference,
                    tags = tags,
                    speakers = speakers,
                    bookmarkedEvents = bookmarks,
                    locations = locations,
                )

        if (event.isNullOrEmpty()) {
            Timber.e("Could not find Content with id: $id")
            return null
        }

        // todo: handle multiple events with the same id
        return event.first()
    }

    private suspend fun getFirebaseContentOrNull(
        conference: String,
        id: Long,
    ): FirebaseContent? {
        val snapshot =
            firestore.collection("conferences")
                .document(conference)
                .collection("content")
                .document(id.toString())
                .get()
                .await()
        return snapshot.toObjectOrNull(FirebaseContent::class.java)
    }
}

// todo: this needs to be recursive.
private fun List<Location>.flatten(): List<Location> {
    return flatMap { location ->
        listOf(location) + location.children.flatten()
    }
}
