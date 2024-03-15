package com.advice.core.ui

import com.advice.core.local.Document

data class InformationState(
    val documents: List<Document> = emptyList(),
    val hasWifi: Boolean = false,
    val hasVillages: Boolean = false,
    val hasVendors: Boolean = false,
)
