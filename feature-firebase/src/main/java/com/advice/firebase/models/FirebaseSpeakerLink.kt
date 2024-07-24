package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseSpeakerLink(
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",
    @get:PropertyName("url")
    @set:PropertyName("url")
    var url: String = "",
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = 0,
) : Parcelable
