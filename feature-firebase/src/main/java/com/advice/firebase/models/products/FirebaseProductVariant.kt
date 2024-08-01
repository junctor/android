package com.advice.firebase.models.products

import com.google.firebase.firestore.PropertyName

data class FirebaseProductVariant(
    @get:PropertyName("variant_id")
    @set:PropertyName("variant_id")
    var variantId: Long = -1,
    @get:PropertyName("product_id")
    @set:PropertyName("product_id")
    var productId: Long = -1,
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = -1,
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",
    @get:PropertyName("code")
    @set:PropertyName("code")
    var code: String = "",
    @get:PropertyName("price")
    @set:PropertyName("price")
    var price: Long = -1L,
    @get:PropertyName("stock_status")
    @set:PropertyName("stock_status")
    var stockStatus: String = "OUT",
    @get:PropertyName("tag_ids")
    @set:PropertyName("tag_ids")
    var tags: List<Long> = emptyList()
)
