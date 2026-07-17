package com.advice.firebase.extensions

import com.advice.firebase.models.FirebaseContent
import com.advice.firebase.models.FirebaseDocument
import com.advice.firebase.models.FirebaseSpeaker
import com.advice.firebase.models.organization.FirebaseOrganization
import com.advice.firebase.models.products.FirebaseProduct
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AudienceRestrictionMapperTest {
    @Test
    fun mapsVisibleAgeMinForAllAudienceDtos() {
        assertEquals(18, FirebaseContent(visibleAgeMin = 18).audienceRestriction.minimumAge)
        assertEquals(21, FirebaseSpeaker(visibleAgeMin = 21).audienceRestriction.minimumAge)
        assertEquals(13, FirebaseDocument(visibleAgeMin = 13).audienceRestriction.minimumAge)
        assertEquals(16, FirebaseOrganization(visibleAgeMin = 16).audienceRestriction.minimumAge)
        assertEquals(18, FirebaseProduct(visibleAgeMin = 18).audienceRestriction.minimumAge)
    }

    @Test
    fun mapsNullVisibleAgeMinAsUnrestricted() {
        assertNull(FirebaseContent().audienceRestriction.minimumAge)
        assertNull(FirebaseSpeaker().audienceRestriction.minimumAge)
        assertNull(FirebaseDocument().audienceRestriction.minimumAge)
        assertNull(FirebaseOrganization().audienceRestriction.minimumAge)
        assertNull(FirebaseProduct().audienceRestriction.minimumAge)
    }

    @Test
    fun mapsAudienceLabelsFromKnownFields() {
        assertEquals("Talk", FirebaseContent(title = "Talk").audienceLabel)
        assertEquals("Ada", FirebaseSpeaker(name = "Ada").audienceLabel)
        assertEquals("Code of Conduct", FirebaseDocument(titleText = "Code of Conduct").audienceLabel)
        assertEquals("Village", FirebaseOrganization(name = "Village").audienceLabel)
        assertEquals("Shirt", FirebaseProduct(title = "Shirt").audienceLabel)
    }
}
