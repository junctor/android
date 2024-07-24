package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseAffiliation(
    @get:PropertyName("organization")
    @set:PropertyName("organization")
    var organization: String = "",
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",
) : Parcelable
