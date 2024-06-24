package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseContent(
    @PropertyName("id")
    val id: Long = -1,
    @PropertyName("title")
    val title: String = "",
    @PropertyName("description")
    val description: String = "",
    @PropertyName("media")
    val media: List<FirebaseMedia> = emptyList(),
    @PropertyName("people")
    val people: ArrayList<FirebasePerson> = ArrayList(),
    @PropertyName("sessions")
    val sessions: ArrayList<FirebaseSession> = ArrayList(),
    @PropertyName("links")
    val links: List<FirebaseAction> = emptyList(),
    @PropertyName("tag_ids")
    val tag_ids: List<Long> = emptyList(),
    @PropertyName("updated_timestamp")
    val updated_timestamp: Timestamp = Timestamp.now(),
    @PropertyName("hidden")
    val hidden: Boolean = false,
) : Parcelable
