package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseEvent(
    @PropertyName("id")
    val id: Long = -1,
    @PropertyName("conference")
    val conference: String = "",
    @PropertyName("timezone")
    val timezone: String = "",
    @PropertyName("title")
    val title: String = "",
    @PropertyName("description")
    val description: String = "",
    @PropertyName("people")
    val people: ArrayList<FirebasePerson> = ArrayList(),
    @PropertyName("location")
    val location: FirebaseLocation = FirebaseLocation(),
    @PropertyName("links")
    val links: List<FirebaseAction> = emptyList(),

    val tag_ids: List<Long> = emptyList(),

    @PropertyName("begin_timestamp")
    val begin_timestamp: Timestamp = Timestamp.now(),
    @PropertyName("end_timestamp")
    val end_timestamp: Timestamp = Timestamp.now(),
    @PropertyName("updated_timestamp")
    val updated_timestamp: Timestamp = Timestamp.now(),
    @PropertyName("hidden")
    val hidden: Boolean = false
) : Parcelable
