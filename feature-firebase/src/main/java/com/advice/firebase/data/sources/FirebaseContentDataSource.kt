package com.advice.firebase.data.sources

import com.advice.core.audience.AudienceContext
import com.advice.core.audience.AudiencePolicy
import com.advice.core.local.Bookmark
import com.advice.core.local.Conference
import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.Event
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
import com.advice.firebase.extensions.audienceLabel
import com.advice.firebase.extensions.audienceRestriction
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.mapSnapshot
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toContents
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.extensions.unwrapList
import com.advice.firebase.models.FirebaseContent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class FirebaseContentDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    tagsDataSource: TagsDataSource,
    speakersDataSource: SpeakersDataSource,
    locationsDataSource: LocationsDataSource,
    feedbackDataSource: FeedbackDataSource,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
    private val audiencePolicy: AudiencePolicy,
    private val applicationScope: CoroutineScope,
) : ContentDataSource {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val content: StateFlow<List<FirebaseContent>> =
        userSession
            .getConference()
            .flatMapLatest { conference ->
                firestore
                    .collection("conferences")
                    .document(conference.code)
                    .collection("content")
                    .snapshotFlow()
                    .closeOnConferenceChange(userSession.getConference())
                    .mapSnapshot { querySnapshot ->
                        querySnapshot
                            .toObjectsOrEmpty(FirebaseContent::class.java)
                            .filter { (!it.hidden || userSession.isDeveloper) }
                    }
                    .unwrapList("Failed to load content")
            }.stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

    private val _conferenceContent =
        combine(
            userSession.getConference(),
            userSession.audienceContext,
            tagsDataSource.get(),
            speakersDataSource.get(),
            combine(
                locationsDataSource.get(),
                feedbackDataSource.get(),
                content,
                bookmarkedEventsDataSource.get(),
            ) { locations, feedbackforms, firebaseContent, bookmarks ->
                AdditionalData(locations, feedbackforms, firebaseContent, bookmarks)
            },
        ) { conference, context, tags, speakers, additional ->
            val content =
                getConferenceContent(
                    conference = conference,
                    context = context,
                    tags = tags,
                    speakers = speakers,
                    locations = additional.locations,
                    feedbackforms = additional.feedbackforms,
                    firebaseContent = additional.firebaseContent,
                    bookmarks = additional.bookmarks,
                )

            ConferenceContent(
                content = content ?: emptyList(),
            )
        }.shareIn(
            scope = applicationScope,
            started = SharingStarted.Lazily,
            replay = 1,
        )

    private data class AdditionalData(
        val locations: List<Location>,
        val feedbackforms: List<FeedbackForm>,
        val firebaseContent: List<FirebaseContent>,
        val bookmarks: List<Bookmark>,
    )

    private fun getConferenceContent(
        conference: Conference,
        context: AudienceContext,
        tags: List<TagType>,
        speakers: List<Speaker>,
        locations: List<Location>,
        feedbackforms: List<FeedbackForm>,
        firebaseContent: List<FirebaseContent>,
        bookmarks: List<Bookmark>,
    ): List<Content>? {
        // Return null if any of the required data is empty.
        if (tags.isEmpty() || locations.isEmpty() || firebaseContent.isEmpty()) {
            return null
        }

        val content =
            firebaseContent.mapNotNull { firebaseItem ->
                if (!audiencePolicy.canView(
                        firebaseItem.audienceRestriction,
                        context,
                        firebaseItem.audienceLabel,
                    )
                ) {
                    return@mapNotNull null
                }
                firebaseItem.toContents(
                    code = conference.code,
                    tags = tags,
                    speakers = speakers,
                    bookmarkedEvents = bookmarks,
                    locations = locations.flatten(),
                    feedbackforms = feedbackforms,
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

    override suspend fun isBookmarked(content: Content): Boolean = bookmarkedEventsDataSource.isBookmarked(content)

    override suspend fun isBookmarked(session: Session): Boolean = bookmarkedEventsDataSource.isBookmarked(session)

    override suspend fun getContent(
        conference: String,
        contentId: Long,
    ): Content? =
        _conferenceContent.first().content.find {
            it.conference == conference && it.id == contentId
        }

    override suspend fun getEvent(
        conference: String,
        contentId: Long,
        sessionId: Long,
    ): Event? {
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
}

// todo: this needs to be recursive.
private fun List<Location>.flatten(): List<Location> =
    flatMap { location ->
        listOf(location) + location.children.flatten()
    }
