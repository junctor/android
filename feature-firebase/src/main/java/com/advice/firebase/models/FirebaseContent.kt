package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseContent(
    @PropertyName("description")
    val description: String = "",
    @PropertyName("feedback_enable_tsz")
    val feedback_enable_tsz: String? = null,
    @PropertyName("feedback_disable_tsz")
    val feedback_disable_tsz: String? = null,
    @PropertyName("feedback_form_id")
    val feedback_form_id: Long? = null,
    @PropertyName("feedback_enable_timestamp")
    val feedback_enable_timestamp: Timestamp? = null,
    @PropertyName("feedback_disable_timestamp")
    val feedback_disable_timestamp: Timestamp? = null,
    @PropertyName("id")
    val id: Long = -1,
    @PropertyName("links")
    val links: List<FirebaseAction> = emptyList(),
    @PropertyName("logo")
    val logo: FirebaseMedia? = null,
    @PropertyName("media")
    val media: List<FirebaseMedia> = emptyList(),
    @PropertyName("people")
    val people: ArrayList<FirebasePerson> = ArrayList(),
    @PropertyName("sessions")
    val sessions: ArrayList<FirebaseSession> = ArrayList(),
    @PropertyName("tag_ids")
    val tag_ids: List<Long> = emptyList(),
    @PropertyName("title")
    val title: String = "",
    @PropertyName("updated_timestamp")
    val updated_timestamp: Timestamp = Timestamp.now(),
    @PropertyName("updated_tsz")
    val updated_tsz: String = "",
    @PropertyName("hidden")
    val hidden: Boolean = false,
) : Parcelable
