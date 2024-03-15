package com.advice.firebase.models.products

data class FirebaseProductVariant(
    val variant_id: Long = -1,
    val product_id: Long = -1,
    val sort_order: Int = -1,
    val title: String = "",
    val code: String = "",
    val price: Long = -1L,
    val stock_status: String = "OUT",
    val tags: List<Long> = emptyList()
)