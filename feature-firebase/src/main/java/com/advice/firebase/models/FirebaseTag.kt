package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseTag(
    val id: Long = -1,
    val label: String = "",
    val description: String = "",
    val color_background: String? = null,
    val color_foreground: String? = null,
    val sort_order: Int = 0,

    // todo: move to client model
    var isSelected: Boolean = false
) : Parcelable