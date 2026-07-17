package com.advice.core.audience

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FailOpenAudiencePolicyTest {
    private val policy = FailOpenAudiencePolicy()

    @Test
    fun noRestriction_allowsAnyContext() {
        val restriction = AudienceRestriction(minimumAge = null)

        assertTrue(policy.canView(restriction, AudienceContext.Unresolved))
        assertTrue(policy.canView(restriction, AudienceContext.Unavailable))
        assertTrue(
            policy.canView(
                restriction,
                AudienceContext.Resolved(lowerAge = 10, status = AudienceStatus.Verified),
            ),
        )
    }

    @Test
    fun unresolvedContext_allowsRestrictedContent() {
        assertTrue(
            policy.canView(
                AudienceRestriction(minimumAge = 18),
                AudienceContext.Unresolved,
            ),
        )
    }

    @Test
    fun unavailableContext_allowsRestrictedContent() {
        assertTrue(
            policy.canView(
                AudienceRestriction(minimumAge = 18),
                AudienceContext.Unavailable,
            ),
        )
    }

    @Test
    fun resolvedUnknownAge_allowsRestrictedContent() {
        assertTrue(
            policy.canView(
                AudienceRestriction(minimumAge = 18),
                AudienceContext.Resolved(lowerAge = null, status = AudienceStatus.Unknown),
            ),
        )
    }

    @Test
    fun belowMinimum_denies() {
        assertFalse(
            policy.canView(
                AudienceRestriction(minimumAge = 18),
                AudienceContext.Resolved(lowerAge = 17, status = AudienceStatus.Verified),
            ),
        )
    }

    @Test
    fun exactBoundary_allows() {
        assertTrue(
            policy.canView(
                AudienceRestriction(minimumAge = 18),
                AudienceContext.Resolved(lowerAge = 18, status = AudienceStatus.Verified),
            ),
        )
    }

    @Test
    fun aboveMinimum_allows() {
        assertTrue(
            policy.canView(
                AudienceRestriction(minimumAge = 18),
                AudienceContext.Resolved(lowerAge = 21, status = AudienceStatus.Verified),
            ),
        )
    }

    @Test
    fun statusDoesNotIndependentlyAlterEligibility() {
        val restriction = AudienceRestriction(minimumAge = 18)
        val ages = listOf(17, 18)

        for (status in AudienceStatus.entries) {
            assertFalse(
                "status=$status should still deny underage",
                policy.canView(
                    restriction,
                    AudienceContext.Resolved(lowerAge = ages[0], status = status),
                ),
            )
            assertTrue(
                "status=$status should still allow boundary age",
                policy.canView(
                    restriction,
                    AudienceContext.Resolved(lowerAge = ages[1], status = status),
                ),
            )
        }
    }
}
