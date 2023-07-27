package com.advice.core.local

data class TagType(
    val id: Long,
    val label: String,
    val category: String,
    val isBrowsable: Boolean,
    val sortOrder: Int,
    val tags: List<Tag>
)
