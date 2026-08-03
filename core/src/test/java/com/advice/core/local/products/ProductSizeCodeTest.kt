package com.advice.core.local.products

import com.advice.core.local.StockStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductSizeCodeTest {
    @Test
    fun fromCode_parsesRecognizedSizesCaseInsensitively() {
        assertEquals(ProductSizeCode.XS, ProductSizeCode.fromCode("xs"))
        assertEquals(ProductSizeCode.M, ProductSizeCode.fromCode(" M "))
        assertEquals(ProductSizeCode.SIX_X, ProductSizeCode.fromCode("6X"))
        assertEquals(ProductSizeCode.XXL, ProductSizeCode.fromCode("2XL"))
    }

    @Test
    fun fromCode_returnsNullForUnrecognized() {
        assertNull(ProductSizeCode.fromCode(""))
        assertNull(ProductSizeCode.fromCode("OS"))
        assertNull(ProductSizeCode.fromCode("Large"))
        assertNull(ProductSizeCode.fromCode("MEDIUM"))
    }

    @Test
    fun sizeCode_usesCodeOnlyNeverLabel() {
        val recognized =
            ProductVariant(1, "Large", emptyList(), 1000, StockStatus.IN_STOCK, code = "L")
        val unrecognized =
            ProductVariant(2, "L", emptyList(), 1000, StockStatus.IN_STOCK, code = "LARGE")
        val blankCode =
            ProductVariant(3, "M", emptyList(), 1000, StockStatus.IN_STOCK, code = "")

        assertEquals(ProductSizeCode.L, recognized.sizeCode)
        assertNull(unrecognized.sizeCode)
        assertNull(blankCode.sizeCode)
    }
}
