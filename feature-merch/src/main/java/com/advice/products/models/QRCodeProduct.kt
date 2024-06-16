package com.advice.products.models

import com.google.gson.annotations.SerializedName

data class QRCodeProduct(
    @SerializedName("q")
    val quantity: Int,
    @SerializedName("v")
    val variant: Long,
)
