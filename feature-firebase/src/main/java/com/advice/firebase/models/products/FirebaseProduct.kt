package com.advice.firebase.models.products

data class FirebaseProduct(
    val id: Long = -1L,
    val code: String = "",
    val title: String = "",
    val description: String = "",
    val price_min: Long = -1L,
    val price_max: Long = -1L,
    val product_id: Long = -1,
    val sort_order: Int = -1,
    val is_eligibility_restricted: String = "N",
    val eligibility_restriction_text: String? = null,
    val media: List<FirebaseProductMedia> = emptyList(),
    val tags: List<Long> = emptyList(),
    val variants: List<FirebaseProductVariant> = emptyList()
)
