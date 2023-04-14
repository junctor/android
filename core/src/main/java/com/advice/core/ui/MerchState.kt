package com.advice.core.ui

import com.advice.core.local.Merch
import com.advice.core.local.MerchDataModel
import com.advice.core.local.MerchSelection

data class MerchState(
    val elements: List<Merch>,
    val hasDiscount: Boolean = false
)