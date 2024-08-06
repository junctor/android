package com.advice.firebase.data.sources

import com.advice.core.local.Bookmark
import com.advice.core.local.Conference
import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.core.local.FlowResult
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.core.local.Speaker
import com.advice.core.local.TagType
import com.advice.core.local.feedback.FeedbackForm
import com.advice.data.session.UserSession
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.ContentDataSource
import com.advice.data.sources.FeedbackDataSource
import com.advice.data.sources.LocationsDataSource
import com.advice.data.sources.SpeakersDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlowLegacy
import com.advice.firebase.extensions.toContents
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.FirebaseContent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseContentDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val tagsDataSource: TagsDataSource,
    private val speakersDataSource: SpeakersDataSource,
    private val locationsDataSource: LocationsDataSource,
    private val feedbackDataSource: FeedbackDataSource,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
) : ContentDataSource {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val content: StateFlow<List<FirebaseContent>> =
        userSession.getConference().flatMapMerge { conference ->
            firestore
                .collection("conferences")
                .document(conference.code)
                .collection("content")
                .snapshotFlowLegacy()
                .closeOnConferenceChange(userSession.getConference())
                .map { querySnapshot ->
                    querySnapshot
                        .toObjectsOrEmpty(FirebaseContent::class.java)
                        .filter { (!it.hidden || userSession.isDeveloper) }
                }
                .onStart { emit(emptyList()) }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    private val _conferenceContent =
        combine(
            userSession.getConference(),
            tagsDataSource.get(),
            speakersDataSource.get(),
            locationsDataSource.get(),
            feedbackDataSource.get(),
            content,
            bookmarkedEventsDataSource.get(),
        ) { array ->
            val conference = array[0] as Conference
            val tags = array[1] as List<TagType>
            val speakers = array[2] as List<Speaker>
            val locations = array[3] as List<Location>
            val feedbackforms = array[4] as FlowResult<List<FeedbackForm>>
            val firebaseContent = array[5] as List<FirebaseContent>
            val bookmarks = array[6] as List<Bookmark>

            val content = getConferenceContent(
                conference = conference,
                tags = tags,
                speakers = speakers,
                locations = locations,
                feedbackforms = feedbackforms,
                firebaseContent = firebaseContent,
                bookmarks = bookmarks,
            )

            ConferenceContent(
                content = content ?: emptyList(),
            )
        }.shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1,
        )

    private fun getConferenceContent(
        conference: Conference,
        tags: List<TagType>,
        speakers: List<Speaker>,
        locations: List<Location>,
        feedbackforms: FlowResult<List<FeedbackForm>>,
        firebaseContent: List<FirebaseContent>,
        bookmarks: List<Bookmark>
    ): List<Content>? {
        if (tags.isEmpty() || locations.isEmpty() || firebaseContent.isEmpty()) {
            Timber.e("Could not fetch conference content: ${conference.code}, tags: ${tags.size}, locations: ${locations.size}, content: ${firebaseContent.size}")
            return null
        }

        val content = firebaseContent.mapNotNull {
            it.toContents(
                code = conference.code,
                tags = tags,
                speakers = speakers,
                bookmarkedEvents = bookmarks,
                locations = locations.flatten(),
                feedbackforms = feedbackforms.toResultOrNull() ?: emptyList(),
            )
        }

        return content
    }

    override fun get(): Flow<ConferenceContent> = _conferenceContent

    override suspend fun bookmark(content: Content) {
        bookmarkedEventsDataSource.bookmark(content, isBookmarked = !content.isBookmarked)
    }

    override suspend fun bookmark(session: Session) {
        bookmarkedEventsDataSource.bookmark(session, isBookmarked = !session.isBookmarked)
    }

    override suspend fun isBookmarked(content: Content): Boolean {
        return bookmarkedEventsDataSource.isBookmarked(content)
    }

    override suspend fun isBookmarked(session: Session): Boolean {
        return bookmarkedEventsDataSource.isBookmarked(session)
    }

    override suspend fun getContent(conference: String, contentId: Long): Content? {
        val tags = tagsDataSource.fetch(conference)
        val speakers = speakersDataSource.fetch(conference)
        val locations = locationsDataSource.fetch(conference).flatten()
        val bookmarks = bookmarkedEventsDataSource.get().first()
        val feedbackforms = feedbackDataSource.fetch(conference)

        val firebaseContent = getFirebaseContentOrNull(conference, contentId)
        if (firebaseContent == null) {
            Timber.e("Content not found: $contentId")
            return null
        }

        val content = firebaseContent.toContents(
            code = conference,
            tags = tags,
            speakers = speakers,
            bookmarkedEvents = bookmarks,
            locations = locations,
            feedbackforms = feedbackforms,
        )

        if (content == null) {
            Timber.e("Could not map FirebaseContent to Content: $contentId")
            return null
        }

        return content
    }

    override suspend fun getEvent(conference: String, contentId: Long, sessionId: Long): Event? {
        val content = getContent(conference, contentId)

        if (content == null) {
            Timber.e("Could not map FirebaseContent to Content: $contentId")
            return null
        }

        val session = content.sessions.find { it.id == sessionId }
        if (session == null) {
            Timber.e("Could not find session: $sessionId")
            return null
        }

        return Event(content, session)
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
