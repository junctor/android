package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseMap(
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",
    @get:PropertyName("file")
    @set:PropertyName("file")
    var file: String = "",
    @get:PropertyName("filename")
    @set:PropertyName("filename")
    var filename: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("name_text")
    @set:PropertyName("name_text")
    var nameText: String = "",
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = 0,
    @get:PropertyName("url")
    @set:PropertyName("url")
    var url: String? = null,
) : Parcelable
