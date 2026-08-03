package com.advice.merch.presentation.viewmodel

import com.advice.core.local.products.ProductVariantSelection
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductCartTest {
    @Test
    fun add_mergesSameIdAndVariantQuantities() {
        val cart = ProductCart()
        cart.add(ProductVariantSelection(id = 1, variant = 10, quantity = 1))
        cart.add(ProductVariantSelection(id = 1, variant = 10, quantity = 2))

        assertEquals(listOf(ProductVariantSelection(1, 10, 3)), cart.getSelections())
    }

    @Test
    fun add_appendsDifferentVariant() {
        val cart = ProductCart()
        cart.add(ProductVariantSelection(id = 1, variant = 10, quantity = 1))
        cart.add(ProductVariantSelection(id = 1, variant = 11, quantity = 1))

        assertEquals(2, cart.getSelections().size)
    }

    @Test
    fun setQuantityZero_removesSelection() {
        val cart = ProductCart()
        cart.add(ProductVariantSelection(id = 1, variant = 10, quantity = 2))
        cart.setQuantity(id = 1, quantity = 0, variant = 10)

        assertTrue(cart.getSelections().isEmpty())
    }

    @Test
    fun setQuantity_updatesExisting() {
        val cart = ProductCart()
        cart.add(ProductVariantSelection(id = 1, variant = 10, quantity = 2))
        cart.setQuantity(id = 1, quantity = 5, variant = 10)

        assertEquals(listOf(ProductVariantSelection(1, 10, 5)), cart.getSelections())
    }

    @Test
    fun clear_removesAll() {
        val cart = ProductCart()
        cart.add(ProductVariantSelection(id = 1, variant = 10, quantity = 1))
        cart.add(ProductVariantSelection(id = 2, variant = null, quantity = 1))
        cart.clear()

        assertTrue(cart.getSelections().isEmpty())
    }

    @Test
    fun add_appendsDifferentProducts() {
        val cart = ProductCart()
        cart.add(ProductVariantSelection(id = 1, variant = 10, quantity = 1))
        cart.add(ProductVariantSelection(id = 2, variant = 20, quantity = 2))

        assertEquals(
            listOf(
                ProductVariantSelection(1, 10, 1),
                ProductVariantSelection(2, 20, 2),
            ),
            cart.getSelections(),
        )
    }

    @Test
    fun setQuantity_missingIdIsNoOp() {
        val cart = ProductCart()
        cart.add(ProductVariantSelection(id = 1, variant = 10, quantity = 2))
        cart.setQuantity(id = 99, quantity = 5, variant = 10)

        assertEquals(listOf(ProductVariantSelection(1, 10, 2)), cart.getSelections())
    }
}
