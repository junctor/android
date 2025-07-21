package com.advice.products.utils

import com.advice.core.local.products.ProductVariantSelection
import junit.framework.TestCase.assertEquals
import org.junit.Test

class QRCodeUtilsKtTest {

    // 1:123:A726:456:2;789:3
    @Test
    fun `test generate qr code data string`() {

        val products = listOf(
            ProductVariantSelection(id = 1, variant = 456, quantity = 2),
            ProductVariantSelection(id = 2, variant = 789, quantity = 3),
        )

        val data = products.toStringData(conference = 123, platformVersion = "7.2.6")

        assertEquals("String data does not match", "1:123:A726:456:2;789:3", data)
    }
}
