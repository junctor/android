package com.advice.schedule.navigation

import com.advice.core.local.MenuItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MenuItemNavigationTest {
    @Test
    fun `maps known menu functions to typed navigation`() {
        assertEquals(
            Navigation.News("News"),
            MenuItem.Navigation("", "News", null, "news").toNavigation(),
        )
        assertEquals(
            Navigation.Locations("Locations"),
            MenuItem.Navigation("", "Locations", null, "locations").toNavigation(),
        )
        assertEquals(
            Navigation.People("People"),
            MenuItem.Navigation("", "People", null, "people").toNavigation(),
        )
        assertEquals(
            Navigation.Products("Merch"),
            MenuItem.Navigation("", "Merch", null, "products").toNavigation(),
        )
        assertEquals(
            Navigation.FAQ("FAQ"),
            MenuItem.Navigation("", "FAQ", null, "faq").toNavigation(),
        )
    }

    @Test
    fun `unknown menu function returns null`() {
        assertNull(
            MenuItem.Navigation("", "Unknown", null, "mystery").toNavigation(),
        )
    }
}
