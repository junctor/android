package com.advice.products.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.StockStatus
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductMedia
import com.advice.core.local.products.ProductSelection
import com.advice.core.local.products.ProductVariant
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.DismissibleInformation
import com.advice.products.utils.toStringData

class ProductsProvider : PreviewParameterProvider<ProductsState> {
    override val values: Sequence<ProductsState>
        get() {
            val options = listOf(
                ProductVariant(1, "S", emptyList(), 35_00, StockStatus.IN_STOCK),
                ProductVariant(2, "4XL", emptyList(), 40_00, StockStatus.LOW_STOCK),
                ProductVariant(3, "5XL", emptyList(), 45_00, StockStatus.OUT_OF_STOCK)
            )
            val tag = Tag(1L, "Clothing", "T-Shirts", "#FF066", -1)
            val product = Product(
                id = -1L,
                code = "07",
                sortOrder = -1,
                label = "DC30 Homecoming Men's T-Shirt",
                baseCost = 35_00,
                variants = options,
                media = listOf(
                    ProductMedia(
                        url = "https://firebasestorage.googleapis.com/v0/b/hackertest-5a202.appspot.com/o/DEFCON30%2Fm_pride_tee.jpeg?alt=media",
                    ),
                    ProductMedia(
                        url = "https://firebasestorage.googleapis.com/v0/b/hackertest-5a202.appspot.com/o/DEFCON30%2Fm_pride_tee.jpeg?alt=media",
                    )
                ),
                tags = listOf(tag),
            )

            val elements = listOf(
                product.copy(),
                product.copy(variants = listOf(options.first())),
                product.copy(variants = listOf(options.last())),
            )

            val cart = listOf(
                ProductSelection(product, options.first(), 1),
                ProductSelection(product, options.last(), 1),
            )

            return listOf(
                ProductsState(
                    groups = mapOf(tag to elements),
                    productVariantTagTypes = listOf(
                        TagType(
                            1L,
                            "Variants",
                            "Size",
                            true,
                            -1,
                            listOf(
                                Tag(1L, "Clothing", "T-Shirts", "#FF066", -1),
                                Tag(2L, "Clothing", "Pants", "#FF066", -1),
                                Tag(3L, "Clothing", "Shoes", "#FF066", -1)
                            )
                        )
                    ),
                    informationList = listOf(
                        DismissibleInformation(
                            key = "general",
                            text = "Important Information",
                            document = 1,
                        ),
                        DismissibleInformation(
                            key = "merch",
                            text = "Merchandise Acknowledgement",
                            document = null,
                        ),
                    ),
                    merchMandatoryAcknowledgement = "All sales are **CASH ONLY**. Prices include Nevada State Sales Tax.",
                    merchTaxStatement = "Prices include Nevada State Sales Tax.",
                    cart = cart,
                    data = cart.toStringData(conference = 133, "1.7.0"),
                )
            ).asSequence()
        }
}
