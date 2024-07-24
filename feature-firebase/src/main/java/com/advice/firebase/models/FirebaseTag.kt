package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseTag(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("label")
    @set:PropertyName("label")
    var label: String = "",
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",
    @get:PropertyName("color_background")
    @set:PropertyName("color_background")
    var colorBackground: String? = null,
    @get:PropertyName("color_foreground")
    @set:PropertyName("color_foreground")
    var colorForeground: String? = null,
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = 0,
) : Parcelable
