package com.advice.schedule.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationDestinationTest {
    @Test
    fun `event destination uses three encoded path segments`() {
        val navigation =
            Navigation.Event(
                conference = "DC34",
                id = "7",
                session = "42",
            )

        assertEquals("event/DC34/7/42", navigation.destination())
        assertEquals("event/{conference}/{contentId}/{sessionId}", navigation.route())
    }

    @Test
    fun `event destination encodes special characters in labels`() {
        val navigation =
            Navigation.Event(
                conference = "DC 34",
                id = "7/8",
                session = "a b",
            )

        val destination = navigation.destination()
        assertTrue(destination.startsWith("event/"))
        assertEquals(
            "event/DC%2034/7%2F8/a%20b",
            destination,
        )
    }

    @Test
    fun `news and schedule encode labels`() {
        assertEquals("news/Breaking%20News", Navigation.News("Breaking News").destination())
        assertEquals(
            "schedule/Talks%20%26%20Panels/1,2",
            Navigation.Schedule("Talks & Panels", listOf(1, 2)).destination(),
        )
    }
}
