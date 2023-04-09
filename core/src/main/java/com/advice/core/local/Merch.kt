package com.advice.core.local

data class Merch(
    val label: String,
    val cost: Int,
    val sizes: List<String>,
    val image: Boolean = false,
    val count: Int = 0,
)