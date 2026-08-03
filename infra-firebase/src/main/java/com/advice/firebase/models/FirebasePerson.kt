package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebasePerson(
    @get:PropertyName("person_id")
    @set:PropertyName("person_id")
    var personId: Long = -1L,
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = 0,
    @get:PropertyName("tag_ids")
    @set:PropertyName("tag_ids")
    var tagIds: List<Long> = emptyList(),
) : Parcelable
