package com.advice.schedule.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DeepLinkParserTest {
    @Test
    fun `parses event deep link with colon session id`() {
        assertEquals(
            Navigation.Event("DC34", "7", "42"),
            DeepLinkParser.parse(
                pathSegment = "event",
                conference = "DC34",
                event = "7:42",
                documentId = null,
            ),
        )
    }

    @Test
    fun `parses event deep link without session`() {
        assertEquals(
            Navigation.Event("DC34", "7", ""),
            DeepLinkParser.parse(
                pathSegment = "event",
                conference = "DC34",
                event = "7",
                documentId = null,
            ),
        )
    }

    @Test
    fun `parses document deep link with conference and id`() {
        assertEquals(
            Navigation.Document(99),
            DeepLinkParser.parse(
                pathSegment = "document",
                conference = "DC34",
                event = null,
                documentId = "99",
            ),
        )
    }

    @Test
    fun `rejects document deep link without conference`() {
        assertNull(
            DeepLinkParser.parse(
                pathSegment = "document",
                conference = null,
                event = null,
                documentId = "99",
            ),
        )
    }
}
