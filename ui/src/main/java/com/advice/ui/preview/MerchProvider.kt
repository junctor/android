package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.MerchDataModel
import com.advice.core.local.MerchSelection
import com.advice.core.local.toMerch
import com.advice.core.ui.MerchState

class MerchProvider : PreviewParameterProvider<MerchState> {
    override val values: Sequence<MerchState>
        get() = listOf(
            MerchState(
                elements = listOf(
                    MerchDataModel(
                        "DC30 Homecoming Men's T-Shirt",
                        35,
                        listOf("S", "4XL", "5XL", "6XL"),
                        hasImage = true,
                    ),
                    MerchDataModel("DC30 Homecoming Women's T-Shirt", 35, listOf("XS", "S", "L", "XL")),
                    MerchDataModel(
                        "DC30 Square Men's T-Shirt",
                        35,
                        listOf("S", "4XL", "5XL", "6XL"),
                        hasImage = true,
                    ),
                    MerchDataModel(
                        "DC30 Square Women's T-Shirt",
                        35,
                        listOf("S", "4XL", "5XL", "6XL"),
                        hasImage = true
                    ),
                    MerchDataModel("DC30 Skull T-Shirt", 40, listOf("S", "4XL", "5XL", "6XL")),
                    MerchDataModel("DC30 Signal T-Shirt", 35, listOf("S", "4XL", "5XL", "6XL")),
                    MerchDataModel("DC30 Crown T-Shirt", 50, listOf("S", "4XL", "5XL", "6XL")),
                    MerchDataModel("Pride T-Shirt", 35, listOf("S", "4XL", "5XL", "6XL")),
                    MerchDataModel("D I S O B E Y Pin", 10, listOf(), hasImage = true),
                ).map { it.toMerch() },
                hasDiscount = true,
            )
        ).asSequence()

}