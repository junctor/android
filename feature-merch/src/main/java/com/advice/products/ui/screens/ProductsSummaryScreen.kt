package com.advice.products.ui.screens


import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Product
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.EditableProduct
import com.advice.products.ui.preview.ProductsProvider
import com.advice.ui.components.EmptyView
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsSummaryScreen(
    state: ProductsState,
    onQuantityChanged: (Long, Int, String?) -> Unit,
    onBackPressed: () -> Unit,
) {
    val list = state.cart

    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = { Text("Merch") }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(Icons.Default.Close, null)
            }
        })
    }) {
        if (list.isEmpty()) {
            EmptyView(message = "Merch not found", modifier = Modifier.padding(it))
        } else {
            ProductsSummaryContent(
                list,
                state.json,
                state.hasDiscount,
                Modifier.padding(it),
                onQuantityChanged,
            )
        }
    }
}

@Composable
fun ProductsSummaryContent(
    list: List<Product>,
    json: String?,
    hasDiscount: Boolean,
    modifier: Modifier,
    onQuantityChanged: (Long, Int, String?) -> Unit,
) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        if (json != null) {
            Box(Modifier.fillMaxWidth()) {
                val qrCodeBitmap = generateQRCode(json)
                Image(
                    bitmap = qrCodeBitmap.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier
                        .size(256.dp)
                        .align(Alignment.Center)
                )
            }
        }

        for (merch in list) {
            EditableProduct(merch, onQuantityChanged = {
                onQuantityChanged(merch.id, it, merch.selectedOption)
            })
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val subtotal = getSubtotal(list)
            Text("Subtotal", style = MaterialTheme.typography.titleLarge)
            Text(
                "$${String.format("%.2f", subtotal)} USD",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

fun getSubtotal(list: List<Product>): Float {
    return list.sumOf { element ->
        element.cost.toInt()
    } / 100f
}


@LightDarkPreview
@Composable
fun ProductsSummaryScreenPreview(@PreviewParameter(ProductsProvider::class) state: ProductsState) {
    ScheduleTheme {
        ProductsSummaryScreen(state, { _, _, _ -> }, {})
    }
}