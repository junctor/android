package com.advice.products.utils

import com.advice.core.local.products.ProductVariantSelection
import junit.framework.TestCase.assertEquals
import org.junit.Test

class QRCodeUtilsKtTest {

    // $1%<platform_code%<conference_id>%<txn_number>%<product_variant_id>:<product_variant_quantity>/$
    // $1%A%133%%5432:1/6666:3/1234:2$
    @Test
    fun `test generate qr code data string`() {

        val products = listOf(
            ProductVariantSelection(id = 1, variant = 5432, quantity = 1),
            ProductVariantSelection(id = 2, variant = 6666, quantity = 3),
            ProductVariantSelection(id = 3, variant = 1234, quantity = 2),
        )

        val data = products.toStringData(conference = 133)

        assertEquals("String data does not match", "\$1%A%133%%5432:1/6666:3/1234:2/\$", data)
    }
}
