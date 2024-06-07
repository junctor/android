package com.advice.products.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.products.models.QRCodeData
import com.advice.products.models.QRCodeProduct
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import timber.log.Timber
import java.util.EnumMap

fun List<Product>.toJson(): String? {
    try {
        // if the product is out of stock, we can't generate a QR code
        if (any { it.variant?.stockStatus == StockStatus.OUT_OF_STOCK }) {
            return null
        }

        val data = QRCodeData("", map { QRCodeProduct(it.quantity, it.variant?.id ?: -1L) })

        val gson = Gson()
        val type = object : TypeToken<QRCodeData>() {}.type
        return gson.toJson(data, type)
    } catch (ex: Exception) {
        Timber.e(ex, "Error converting products to JSON")
        return null
    }
}

fun generateQRCode(json: String): Bitmap {
    val width = 400
    val height = 400
    val hintMap: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
    hintMap[EncodeHintType.CHARACTER_SET] = "UTF-8"
    hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L

    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(json, BarcodeFormat.QR_CODE, width, height, hintMap)

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return bitmap
}
