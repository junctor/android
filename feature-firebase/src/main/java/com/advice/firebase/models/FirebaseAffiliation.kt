package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseAffiliation(
    val organization: String = "",
    val title: String = "",
) : Parcelable
