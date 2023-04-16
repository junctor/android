package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.MerchDataModel
import com.advice.core.local.MerchOption
import com.advice.core.local.MerchSelection
import com.advice.core.local.toMerch
import com.advice.core.ui.MerchState

class MerchProvider : PreviewParameterProvider<MerchState> {
    override val values: Sequence<MerchState>
        get() {
            val options = listOf(MerchOption("S", true, 0), MerchOption("4XL", true, 0), MerchOption("5XL", false, 1000))
            return listOf(
                MerchState(
                    elements = listOf(
                        MerchDataModel(
                            "DC30 Homecoming Men's T-Shirt",
                            3500,
                            options,
                            image = "https://image.uniqlo.com/UQ/ST3/WesternCommon/imagesgoods/422990/item/goods_09_422990.jpg?width=750",
                        ),
                        MerchDataModel("DC30 Homecoming Women's T-Shirt", 3500, options),
                        MerchDataModel(
                            "DC30 Square Men's T-Shirt",
                            3500,
                            options,
                        ),
                        MerchDataModel(
                            "DC30 Square Women's T-Shirt",
                            3500,
                            options,
                        ),
                        MerchDataModel("DC30 Skull T-Shirt", 4000,options),
                        MerchDataModel("DC30 Signal T-Shirt", 3500, options),
                        MerchDataModel("DC30 Crown T-Shirt", 5000, options),
                        MerchDataModel("Pride T-Shirt", 3005, options),
                        MerchDataModel("D I S O B E Y Pin", 1000, listOf()),
                    ).map { it.toMerch() },
                    hasDiscount = true,
                )
            ).asSequence()
        }

}