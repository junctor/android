package com.advice.products.models

import com.google.gson.annotations.SerializedName

data class QRCodeData(
    @SerializedName("products")
    val products: List<QRCodeProduct>,
)

data class QRCodeProduct(
    @SerializedName("id")
    val id: Long,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("variant")
    val variant: String?,
)