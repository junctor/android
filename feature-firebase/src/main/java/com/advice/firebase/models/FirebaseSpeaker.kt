package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseSpeaker(
    @get:PropertyName("affiliations")
    @set:PropertyName("affiliations")
    var affiliations: List<FirebaseAffiliation> = emptyList(),
    @get:PropertyName("conference")
    @set:PropertyName("conference")
    var conference: String? = null,
    @get:PropertyName("conference_id")
    @set:PropertyName("conference_id")
    var conferenceId: Long? = null,
    @get:PropertyName("content_ids")
    @set:PropertyName("content_ids")
    var contentIds: List<Long> = emptyList(),
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",
    @Deprecated("Use FirebaseSpeaker.content_ids instead.")
    @get:PropertyName("event_ids")
    @set:PropertyName("event_ids")
    var eventIds: List<Long> = emptyList(),
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("link")
    @set:PropertyName("link")
    var link: String = "",
    @get:PropertyName("links")
    @set:PropertyName("links")
    var links: List<FirebaseSpeakerLink> = emptyList(),
    @get:PropertyName("media")
    @set:PropertyName("media")
    var media: List<FirebaseMedia> = emptyList(),
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("pronouns")
    @set:PropertyName("pronouns")
    var pronouns: String? = null,
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String? = null,
    @Deprecated("Use FirebaseSpeaker.links instead.")
    @get:PropertyName("twitter")
    @set:PropertyName("twitter")
    var twitter: String? = null,
    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: String? = null,
    @get:PropertyName("updated_timestamp")
    @set:PropertyName("updated_timestamp")
    var updatedTimestamp: Timestamp = Timestamp.now(),
    @get:PropertyName("updated_tsz")
    @set:PropertyName("updated_tsz")
    var updatedTsz: String = "",
    @get:PropertyName("hidden")
    @set:PropertyName("hidden")
    var hidden: Boolean = false,
) : Parcelable
