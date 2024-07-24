package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseVendor(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Int = -1,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String? = null,
    @get:PropertyName("link")
    @set:PropertyName("link")
    var link: String? = null,
    @get:PropertyName("partner")
    @set:PropertyName("partner")
    var partner: Boolean = false,
    @get:PropertyName("updatedAt")
    @set:PropertyName("updatedAt")
    var updatedAt: String = "",
    @get:PropertyName("conference")
    @set:PropertyName("conference")
    var conference: String = "",
    @get:PropertyName("hidden")
    @set:PropertyName("hidden")
    var hidden: Boolean = false,
) : Parcelable
