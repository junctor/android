package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseSpeaker(
    val affiliations: List<FirebaseAffiliation> = emptyList(),
    val conference: String? = null,
    val conference_id: Long? = null,
    val content_ids: List<Long> = emptyList(),
    val description: String = "",
    @Deprecated("Use FirebaseSpeaker.content_ids instead.")
    val event_ids: List<Long> = emptyList(),
    @Deprecated("Use FirebaseSpeaker.content_ids instead.")
    val events: List<String> = emptyList(),
    val id: Long = -1,
    val link: String = "",
    val links: List<FirebaseSpeakerLink> = emptyList(),
    val media: List<FirebaseMedia> = emptyList(),
    val name: String = "",
    val pronouns: String? = null,
    val title: String? = null,
    @Deprecated("Use FirebaseSpeaker.links instead.")
    val twitter: String? = null,
    val updated_at: String? = null,
    val updated_timestamp: Timestamp = Timestamp.now(),
    val updated_tsz: String = "",
    val hidden: Boolean = false,
) : Parcelable
