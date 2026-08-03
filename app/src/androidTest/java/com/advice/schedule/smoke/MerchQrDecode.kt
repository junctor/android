package com.advice.schedule.smoke

import android.graphics.Bitmap
import com.advice.merch.utils.generateQRCode
import com.advice.merch.utils.parseCompactOrderData
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import org.junit.Assert.assertEquals

/** Decode a QR [Bitmap] with ZXing. */
internal fun decodeQrBitmap(bitmap: Bitmap): String {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    val source = RGBLuminanceSource(width, height, pixels)
    return MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(source))).text
}

/**
 * Assert [payload] is compact-order and that encoding it with the production
 * [generateQRCode] yields a scannable QR that decodes back to the same string.
 *
 * The summary UI renders via the same encoder, so this validates the cart QR
 * without Compose [captureToImage] (which can hang on never-idle screens).
 */
internal fun assertDecodableCompactQr(payload: String) {
    val order = parseCompactOrderData(payload)
    assertEquals("Compact order version", 1, order.version)
    val roundTrip = decodeQrBitmap(generateQRCode(payload))
    assertEquals("QR encode/decode round-trip", payload, roundTrip)
}
