package com.advice.products.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import com.advice.products.utils.generateQRCode
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun QRCodeImage(json: String, modifier: Modifier = Modifier) {
    val qrCodeBitmap = generateQRCode(json)
    Image(
        bitmap = qrCodeBitmap.asImageBitmap(),
        contentDescription = "QR Code",
        modifier = modifier
    )
}

@PreviewLightDark
@Preview
@Composable
private fun QRCodeImagePreview() {
    ScheduleTheme {
        QRCodeImage("{\"products\":[{\"id\":1,\"quantity\":1,\"variant\":\"M\"},{\"id\":3,\"quantity\":2,\"variant\":\"S\"}]}")
    }
}
