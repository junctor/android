package com.advice.firebase.models.products

import com.google.firebase.firestore.PropertyName

data class FirebaseProductMedia(
    @get:PropertyName("url")
    @set:PropertyName("url")
    var url: String = "",
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = -1,
)
