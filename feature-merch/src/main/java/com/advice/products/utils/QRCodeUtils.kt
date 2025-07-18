package com.advice.products.utils

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.advice.core.local.StockStatus
import com.advice.core.local.products.ProductSelection
import com.advice.core.local.products.ProductVariantSelection
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import timber.log.Timber
import java.util.EnumMap

/**
 * Generate the QR Code data based on a more concise format.
 *
 * Example: $1%133%99999%5432:1/6666:3/1234:2/$
 * $1%<platform_code%<conference_id>%<txn_number>%<product_variant_id>:<product_variant_quantity>/$
 *
 * https://github.com/junctor/post-dc32-triage/issues/2
 */
fun List<ProductSelection>.toStringData(conference: Long?): String? {
    try {
        if (isEmpty()) {
            return null
        }

        if (conference == null) {
            Timber.e("Conference id is null, cannot generate QR code.")
            return null
        }

        // if the product is out of stock, we can't generate a QR code
        if (any { it.variant.stockStatus == StockStatus.OUT_OF_STOCK }) {
            Timber.e("summary contains a product that is out of stock, can't generate QR code.")
            find { it.variant.stockStatus == StockStatus.OUT_OF_STOCK }?.let {
                Timber.e("Product: ${it.label} is out of stock")
            }
            return null
        }

        val products = map { ProductVariantSelection(it.id, it.variant.id, it.quantity) }

        return products.toStringData(conference)
    } catch (ex: Exception) {
        Timber.e(ex, "Error converting products to JSON")
        return null
    }
}

fun List<ProductVariantSelection>.toStringData(conference: Long): String {
    // A is for Android
    val platform = "A"
    // txn is always empty on client app
    val txn = ""
    // mapping each product to "<id>:<quantity>/"
    val products = mapNotNull { "${it.variant}:${it.quantity}/" }.joinToString(separator = "")

    return "\$1%$platform%$conference%$txn%$products\$"
}

fun generateQRCode(data: String): Bitmap {
    val width = 400
    val height = 400
    val hintMap: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
    hintMap[EncodeHintType.CHARACTER_SET] = "UTF-8"
    hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.L

    val qrCodeWriter = QRCodeWriter()
    val bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hintMap)

    val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
        }
    }
    return bitmap
}
