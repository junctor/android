package com.advice.core.local.products

/**
 * Recognized apparel size codes used for merch size filtering.
 * Only these values (from variant [ProductVariant.code]) produce size filter chips.
 */
enum class ProductSizeCode(
    val code: String,
) {
    XS("XS"),
    S("S"),
    M("M"),
    L("L"),
    XL("XL"),
    XXL("2XL"),
    XXXL("3XL"),
    XXXXL("4XL"),
    XXXXXL("5XL"),
    XXXXXXL("6XL"),
    ONE_X("1X"),
    TWO_X("2X"),
    THREE_X("3X"),
    FOUR_X("4X"),
    FIVE_X("5X"),
    SIX_X("6X"),
    ;

    companion object {
        private val byCode = entries.associateBy { it.code.uppercase() }

        fun fromCode(raw: String): ProductSizeCode? = byCode[raw.trim().uppercase()]
    }
}
