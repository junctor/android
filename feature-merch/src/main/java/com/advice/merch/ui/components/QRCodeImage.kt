package com.advice.merch.ui.components

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.advice.merch.R
import com.advice.merch.utils.generateQRCode
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

/**
 * QR bitmap for the cart summary. Content description keeps the
 * `QR Code: {payload}` prefix so smoke tests can decode the payload from semantics.
 */
@Composable
internal fun QRCodeImage(
    json: String,
    modifier: Modifier = Modifier,
) {
    val bitmap by remember(json) {
        mutableStateOf(generateQRCode(json))
    }
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = stringResource(R.string.cd_qr_code, json),
        modifier = modifier,
    )
}

@PreviewLightDark
@Preview
@Composable
private fun QRCodeImagePreview() {
    ScheduleTheme {
        QRCodeImage(
            "1:123:A42:456:2;789:3",
        )
    }
}
