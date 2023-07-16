package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseMap(
    val name_text: String = "",
    val filename: String = "",
) : Parcelable