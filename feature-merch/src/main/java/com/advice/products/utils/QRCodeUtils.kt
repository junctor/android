package com.advice.products.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.advice.core.local.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

fun List<Product>.toJson(): String {
    val simplifiedProducts = map {
        mapOf(
            "id" to it.id,
            "quantity" to it.quantity,
            "variant" to it.selectedOption,
        )
    }

    val gson = Gson()
    val type = object : TypeToken<List<Map<String, Any>>>() {}.type
    return gson.toJson(simplifiedProducts, type)
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