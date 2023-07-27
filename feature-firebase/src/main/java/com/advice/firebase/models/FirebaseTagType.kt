package com.advice.firebase.models

data class FirebaseTagType(
    val conference: String = "",
    val conference_id: Long = -1,

    val id: Long = -1,
    val category: String = "",
    @field:JvmField
    val is_browsable: Boolean = true,
    @field:JvmField
    val is_single_valued: Boolean = false,
    val label: String = "",
    val sort_order: Int = 0,

    val tags: List<FirebaseTag> = emptyList()
)
