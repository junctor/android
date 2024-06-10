package com.advice.firebase.models.organization

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseLink(
    val label: String = "",
    val type: String = "link",
    val url: String = "",
) : Parcelable
