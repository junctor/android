package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseAction(
    val label: String = "",
    val url: String = "",
) : Parcelable
