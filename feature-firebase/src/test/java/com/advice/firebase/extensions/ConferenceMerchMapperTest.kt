package com.advice.firebase.extensions

import com.advice.firebase.models.FirebaseConference
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class ConferenceMerchMapperTest {
    @Test
    fun toConference_mapsMerchFieldsAndFlags() {
        val firebase =
            FirebaseConference(
                id = 34,
                name = "DEF CON 34",
                code = "DC34",
                taglineText = "Hello",
                homeMenuId = 1,
                timezone = "America/Los_Angeles",
                enableMerch = true,
                enableMerchCart = true,
                enableWifi = false,
                merchHelpDocId = 99,
                merchMandatoryAcknowledgement = "Cash only.",
                merchTaxStatement = "Tax included.",
                kickoffTimestamp = ts("2026-08-06T00:00:00Z"),
                startTimestamp = ts("2026-08-07T00:00:00Z"),
                endTimestamp = ts("2026-08-10T00:00:00Z"),
            )

        val conference = firebase.toConference()

        assertNotNull(conference)
        assertEquals(34L, conference!!.id)
        assertEquals("Cash only.", conference.merchInformation?.merchMandatoryAcknowledgement)
        assertEquals("Tax included.", conference.merchInformation?.merchTaxStatement)
        assertEquals(99L, conference.merchInformation?.merchHelpDocId)
        assertTrue(conference.flags["enable_merch"] == true)
        assertTrue(conference.flags["enable_merch_cart"] == true)
        assertFalse(conference.flags["enable_wifi"] == true)
    }

    @Test
    fun toConference_nullMerchAcknowledgementAndDisabledCart() {
        val firebase =
            FirebaseConference(
                id = 1,
                name = "TEST",
                code = "TEST",
                timezone = "UTC",
                enableMerch = false,
                enableMerchCart = false,
                merchMandatoryAcknowledgement = null,
                merchTaxStatement = null,
                kickoffTimestamp = ts("2026-01-01T00:00:00Z"),
                startTimestamp = ts("2026-01-01T00:00:00Z"),
                endTimestamp = ts("2026-01-02T00:00:00Z"),
            )

        val conference = firebase.toConference()

        assertNotNull(conference)
        assertEquals(null, conference!!.merchInformation?.merchMandatoryAcknowledgement)
        assertEquals(null, conference.merchInformation?.merchTaxStatement)
        assertFalse(conference.flags["enable_merch"] == true)
        assertFalse(conference.flags["enable_merch_cart"] == true)
    }

    private fun ts(iso: String): Timestamp = Timestamp(Date.from(java.time.Instant.parse(iso)))
}
