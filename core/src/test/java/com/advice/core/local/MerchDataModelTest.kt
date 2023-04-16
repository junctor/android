package com.advice.core.local

import org.junit.Assert.*
import org.junit.Test

class MerchDataModelTest {

    @Test
    fun `set discount`() {
        val model = MerchDataModel("label", 10_00, emptyList())

        val result = model.toMerch(quantity = 1, discount = 0.10f)

        assertEquals(1, result.quantity)
        assertEquals(10_00, result.cost)
        assertEquals(9_00, result.discountedPrice)
    }

    @Test
    fun `set discount on multiple`() {
        val model = MerchDataModel("label", 10_00, emptyList())

        val result = model.toMerch(quantity = 2, discount = 0.10f)

        assertEquals(2, result.quantity)
        assertEquals(20_00, result.cost)
        assertEquals(18_00, result.discountedPrice)
    }

    @Test
    fun `extra cost from selected option`() {
        val option = MerchOption("option", true, 5_00)
        val model = MerchDataModel("label", 10_00, listOf(option))

        val merch = model.toMerch()
        val result = merch.update(MerchSelection("1", 1, "option",), null)

        assertEquals(1, result.quantity)
        assertEquals(15_00, result.cost)
        assertEquals(null, result.discountedPrice)
    }

    @Test
    fun `extra cost from selected option with discount`() {
        val option = MerchOption("option", true, 5_00)
        val model = MerchDataModel("label", 10_00, listOf(option))

        val merch = model.toMerch()
        val result = merch.update(MerchSelection("1", 1, "option",), 0.10f)

        assertEquals(1, result.quantity)
        assertEquals(15_00, result.cost)
        assertEquals(13_50, result.discountedPrice)
    }
}