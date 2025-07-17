package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseContent(
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",
    @get:PropertyName("feedback_enable_tsz")
    @set:PropertyName("feedback_enable_tsz")
    var feedbackEnableTsz: String? = null,
    @get:PropertyName("feedback_disable_tsz")
    @set:PropertyName("feedback_disable_tsz")
    var feedbackDisableTsz: String? = null,
    @get:PropertyName("feedback_form_id")
    @set:PropertyName("feedback_form_id")
    var feedbackFormId: Long? = null,
    @get:PropertyName("feedback_enable_timestamp")
    @set:PropertyName("feedback_enable_timestamp")
    var feedbackEnableTimestamp: Timestamp? = null,
    @get:PropertyName("feedback_disable_timestamp")
    @set:PropertyName("feedback_disable_timestamp")
    var feedbackDisableTimestamp: Timestamp? = null,
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("links")
    @set:PropertyName("links")
    var links: List<FirebaseAction> = emptyList(),
    @get:PropertyName("logo")
    @set:PropertyName("logo")
    var logo: FirebaseMedia? = null,
    @get:PropertyName("media")
    @set:PropertyName("media")
    var media: List<FirebaseMedia> = emptyList(),
    @get:PropertyName("people")
    @set:PropertyName("people")
    var people: ArrayList<FirebasePerson> = ArrayList(),
    @get:PropertyName("sessions")
    @set:PropertyName("sessions")
    var sessions: ArrayList<FirebaseSession> = ArrayList(),
    @get:PropertyName("tag_ids")
    @set:PropertyName("tag_ids")
    var tag_ids: List<Long> = emptyList(),
    @get:PropertyName("related_content_ids")
    @set:PropertyName("related_content_ids")
    var related_content_ids: List<Long> = emptyList(),
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",
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
