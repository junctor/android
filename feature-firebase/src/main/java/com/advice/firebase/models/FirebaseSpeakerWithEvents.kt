package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseSpeakerWithEvents(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Int = -1,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",
    @get:PropertyName("link")
    @set:PropertyName("link")
    var link: String = "",
    @get:PropertyName("twitter")
    @set:PropertyName("twitter")
    var twitter: String = "",
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",
    @get:PropertyName("conference")
    @set:PropertyName("conference")
    var conference: String? = null,
    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: String? = null,
    @get:PropertyName("events")
    @set:PropertyName("events")
    var events: List<FirebaseContent>? = null,
    @get:PropertyName("hidden")
    @set:PropertyName("hidden")
    var hidden: Boolean = false,
) : Parcelable
