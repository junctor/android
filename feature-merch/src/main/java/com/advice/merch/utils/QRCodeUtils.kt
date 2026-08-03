package com.advice.merch.utils

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
 * Encoded: 1:123:A726:456:2;789:3
 * Decoded: { cc: 123, p: "A726", i: [ { v: 456, q: 2 }, { v: 789, q: 3 } ] }
 *
 * https://github.com/junctor/ht-qrcode/
 */
fun List<ProductSelection>.toStringData(
    conference: Long?,
    versionCode: Int,
): String? {
    try {
        if (isEmpty()) {
            return null
        }

        if (conference == null) {
            Timber.e("Conference id is null, cannot generate QR code.")
            return null
        }

        // Out-of-stock lines stay in the list UI but are omitted from the QR payload
        val available = filter { it.variant.stockStatus != StockStatus.OUT_OF_STOCK }
        if (available.isEmpty()) {
            return null
        }

        val products = available.map { ProductVariantSelection(it.id, it.variant.id, it.quantity) }

        return products.toStringData(conference, versionCode)
    } catch (ex: Exception) {
        Timber.e(ex, "Error converting products to JSON")
        return null
    }
}

fun List<ProductVariantSelection>.toStringData(
    conference: Long,
    versionCode: Int,
): String {
    // Version 1 of the compact encoding scheme
    val version = 1
    // A is for Android 🤖
    val platform = "A$versionCode"
    // txn is always empty on the client
    val txn = ""
    // mapping each line to "<variantId>:<quantity>"
    val items = joinToString(";") { "${it.variant}:${it.quantity}" }
    val compact = "$version:$conference:$platform:$items"
    if (txn.isNotEmpty()) {
        return "$compact:$txn"
    }
    return compact
}

/**
 * Parsed compact merch order payload.
 *
 * Encoded: `1:123:A726:456:2;789:3`
 */
data class CompactOrderData(
    val version: Int,
    val conferenceId: Long,
    val platform: String,
    val items: List<CompactOrderItem>,
) {
    val totalQuantity: Int get() = items.sumOf { it.quantity }
}

data class CompactOrderItem(
    val variantId: Long,
    val quantity: Int,
)

private val compactOrderRegex =
    Regex("""^(\d+):(\d+):(A\d+):(\d+:\d+(?:;\d+:\d+)*)$""")

/**
 * Parse a compact order string produced by [toStringData].
 * @throws IllegalArgumentException if [data] is not a valid non-empty compact order
 */
fun parseCompactOrderData(data: String): CompactOrderData {
    val match =
        compactOrderRegex.matchEntire(data.trim())
            ?: throw IllegalArgumentException("Not a compact order payload: $data")
    val (version, conferenceId, platform, itemsBlob) = match.destructured
    val items =
        itemsBlob.split(";").map { part ->
            val pieces = part.split(":")
            require(pieces.size == 2) { "Invalid cart line: $part" }
            CompactOrderItem(
                variantId = pieces[0].toLong(),
                quantity = pieces[1].toInt(),
            )
        }
    require(items.isNotEmpty()) { "Compact order has no cart lines" }
    require(items.all { it.quantity > 0 }) { "Compact order has non-positive quantity" }
    return CompactOrderData(
        version = version.toInt(),
        conferenceId = conferenceId.toLong(),
        platform = platform,
        items = items,
    )
}

fun generateQRCode(data: String): Bitmap {
    val width = 400
    val height = 400
    val hintMap: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
    hintMap[EncodeHintType.CHARACTER_SET] = "UTF-8"
    hintMap[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M

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
