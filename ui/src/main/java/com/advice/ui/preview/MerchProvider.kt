package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Merch
import com.advice.core.ui.MerchState

class MerchProvider : PreviewParameterProvider<MerchState> {
    override val values: Sequence<MerchState>
        get() = listOf(
            MerchState(
                elements = listOf(
                    Merch(
                        "DC30 Homecoming Men's T-Shirt",
                        35,
                        listOf("S", "4XL", "5XL", "6XL"),
                        image = true,
                        count = 3
                    ),
                    Merch("DC30 Homecoming Women's T-Shirt", 35, listOf("XS", "S", "L", "XL")),
                    Merch(
                        "DC30 Square Men's T-Shirt",
                        35,
                        listOf("S", "4XL", "5XL", "6XL"),
                        image = true,
                        count = 1
                    ),
                    Merch(
                        "DC30 Square Women's T-Shirt",
                        35,
                        listOf("S", "4XL", "5XL", "6XL"),
                        image = true
                    ),
                    Merch("DC30 Skull T-Shirt", 40, listOf("S", "4XL", "5XL", "6XL")),
                    Merch("DC30 Signal T-Shirt", 35, listOf("S", "4XL", "5XL", "6XL")),
                    Merch("DC30 Crown T-Shirt", 50, listOf("S", "4XL", "5XL", "6XL")),
                    Merch("Pride T-Shirt", 35, listOf("S", "4XL", "5XL", "6XL")),
                    Merch("D I S O B E Y Pin", 10, listOf(), image = true),
                )
            )
        ).asSequence()

}