package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Merch
import com.advice.core.local.MerchOption
import com.advice.core.local.ProductMedia
import com.advice.core.ui.MerchState

class MerchProvider : PreviewParameterProvider<MerchState> {
    override val values: Sequence<MerchState>
        get() {
            val options = listOf(
                MerchOption("S", true, 0),
                MerchOption("4XL", true, 0),
                MerchOption("5XL", false, 1000)
            )
            val merch = Merch(
                -1L, "DC30 Homecoming Men's T-Shirt", 35_00, options, media = listOf(
                    ProductMedia(
                        "https://htem2.habemusconferencing.net/temp/dc24front.jpg",
                        0
                    )
                )
            )
            return listOf(
                MerchState(
                    elements = listOf(merch),
                    hasDiscount = true,
                )
            ).asSequence()
        }

}