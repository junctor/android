package com.advice.play

import com.advice.core.audience.AudienceStatus
import com.google.android.play.agesignals.model.AgeRangeSource
import com.google.android.play.agesignals.model.SignificantChangeStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class AgeSignalsMappingTest {
    @Test
    fun mapsAgeRangeSourceTiers() {
        assertEquals(AudienceStatus.Declared, mapAudienceStatus(AgeRangeSource.TIER_A, null))
        assertEquals(AudienceStatus.Supervised, mapAudienceStatus(AgeRangeSource.TIER_B, null))
        assertEquals(AudienceStatus.Verified, mapAudienceStatus(AgeRangeSource.TIER_C, null))
        assertEquals(AudienceStatus.Verified, mapAudienceStatus(AgeRangeSource.TIER_D, null))
        assertEquals(AudienceStatus.Unknown, mapAudienceStatus(null, null))
        assertEquals(AudienceStatus.Unknown, mapAudienceStatus(AgeRangeSource.UNSPECIFIED, null))
    }

    @Test
    fun significantChangeStatusOverridesSource() {
        assertEquals(
            AudienceStatus.Pending,
            mapAudienceStatus(AgeRangeSource.TIER_B, SignificantChangeStatus.PENDING),
        )
        assertEquals(
            AudienceStatus.Denied,
            mapAudienceStatus(AgeRangeSource.TIER_B, SignificantChangeStatus.DECLINED),
        )
        assertEquals(
            AudienceStatus.Supervised,
            mapAudienceStatus(AgeRangeSource.TIER_B, SignificantChangeStatus.APPROVED),
        )
    }
}
