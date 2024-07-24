package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseAction(
    @PropertyName("label")
    val label: String = "",
    @PropertyName("type")
    val type: String = "",
    @PropertyName("url")
    val url: String = "",
) : Parcelable
