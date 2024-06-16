package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebasePerson(
    val person_id: Long = -1L,
    val sort_order: Int = 0,
    val tag_ids: List<Long> = emptyList(),
) : Parcelable
