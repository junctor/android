package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseBookmark(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",
    @get:PropertyName("value")
    @set:PropertyName("value")
    var value: Boolean = false,
) : Parcelable
