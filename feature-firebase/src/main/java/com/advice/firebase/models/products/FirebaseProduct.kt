package com.advice.firebase.models.products

import com.google.firebase.firestore.PropertyName

data class FirebaseProduct(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1L,
    @get:PropertyName("code")
    @set:PropertyName("code")
    var code: String = "",
    @get:PropertyName("title")
    @set:PropertyName("title")
    var title: String = "",
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",
    @get:PropertyName("price_min")
    @set:PropertyName("price_min")
    var priceMin: Long = -1L,
    @get:PropertyName("price_max")
    @set:PropertyName("price_max")
    var priceMax: Long = -1L,
    @get:PropertyName("product_id")
    @set:PropertyName("product_id")
    var productId: Long = -1,
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = -1,
    @get:PropertyName("is_eligibility_restricted")
    @set:PropertyName("is_eligibility_restricted")
    var isEligibilityRestricted: String = "N",
    @get:PropertyName("eligibility_restriction_text")
    @set:PropertyName("eligibility_restriction_text")
    var eligibilityRestrictionText: String? = null,
    @get:PropertyName("media")
    @set:PropertyName("media")
    var media: List<FirebaseProductMedia> = emptyList(),
    @get:PropertyName("tag_ids")
    @set:PropertyName("tag_ids")
    var tags: List<Long> = emptyList(),
    @get:PropertyName("variants")
    @set:PropertyName("variants")
    var variants: List<FirebaseProductVariant> = emptyList()
)
