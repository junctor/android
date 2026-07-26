package com.advice.core.local

import java.time.Instant

data class NewsArticle(
    val id: Int,
    val name: String,
    val text: String,
    val date: Instant?,
)
