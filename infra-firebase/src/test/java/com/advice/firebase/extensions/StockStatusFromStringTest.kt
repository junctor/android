package com.advice.firebase.extensions

import com.advice.core.local.StockStatus
import com.advice.firebase.models.products.FirebaseProductVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StockStatusFromStringTest {
    @Test
    fun fromString_mapsKnownValues() {
        assertEquals(StockStatus.IN_STOCK, StockStatus.fromString("IN"))
        assertEquals(StockStatus.LOW_STOCK, StockStatus.fromString("LOW"))
        assertEquals(StockStatus.OUT_OF_STOCK, StockStatus.fromString("OUT"))
    }

    @Test
    fun fromString_unknownReturnsNull() {
        assertNull(StockStatus.fromString("UNKNOWN"))
        assertNull(StockStatus.fromString(""))
    }

    @Test
    fun productVariantMapper_fallsBackToInStock() {
        val variant =
            FirebaseProductVariant(
                variantId = 1,
                title = "M",
                price = 1000,
                stockStatus = "WEIRD",
            ).toMerchOption()

        assertEquals(StockStatus.IN_STOCK, variant!!.stockStatus)
    }
}
