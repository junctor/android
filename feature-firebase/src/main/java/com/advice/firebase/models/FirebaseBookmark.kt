package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseBookmark(
        val id: String = "",
        val value: Boolean = false
) : Parcelable