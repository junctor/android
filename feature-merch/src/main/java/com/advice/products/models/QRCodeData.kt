package com.advice.products.models

import com.google.gson.annotations.SerializedName

data class QRCodeData(
    @SerializedName("txn")
    val txn: String = "",
    @SerializedName("i")
    val products: List<QRCodeProduct>,
)

data class QRCodeProduct(
    @SerializedName("q")
    val quantity: Int,
    @SerializedName("v")
    val variant: Long,
)
