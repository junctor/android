package com.advice.firebase.models

data class FirebaseMerch(
    val id: Long = -1L,
    val code: String = "",
    val title: String = "",
    val description: String = "",
    val price_min: String = "",
    val price_max: String = "",
    val product_id: Long = -1,
    val sort_order: Int = -1,
    val is_eligibility_restricted: String = "N",
    val eligibility_restriction_text: String? = null,
    val media: List<FirebaseProductMedia> = emptyList(),
    val tags: List<Long> = emptyList(),
    val variants: List<FirebaseProductVariant> = emptyList()
)

data class FirebaseProductMedia(
    val url: String = "",
    val sort_order: Int = -1,
)

data class FirebaseProductVariant(
    val variant_id: Long = -1,
    val product_id: Long = -1,
    val sort_order: Int = -1,
    val title: String = "",
    val code: String = "",
    val stock_status: String = "IN",
    val price: String = "",
    val tags: List<Long> = emptyList()
)