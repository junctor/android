package com.advice.firebase.models.organization

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseLink(
    @get:PropertyName("label")
    @set:PropertyName("label")
    var label: String = "",
    @get:PropertyName("type")
    @set:PropertyName("type")
    var type: String = "link",
    @get:PropertyName("url")
    @set:PropertyName("url")
    var url: String = "",
) : Parcelable
