package com.advice.core.audience

import com.shortstack.core.BuildConfig
import timber.log.Timber

interface AudiencePolicy {
    fun canView(
        restriction: AudienceRestriction,
        context: AudienceContext,
        label: String? = null,
    ): Boolean
}

class FailOpenAudiencePolicy : AudiencePolicy {
    override fun canView(
        restriction: AudienceRestriction,
        context: AudienceContext,
        label: String?,
    ): Boolean {
        val minAge = restriction.minimumAge ?: return true

        val lowerAge =
            when (context) {
                AudienceContext.Unresolved,
                AudienceContext.Unavailable,
                -> return true
                is AudienceContext.Resolved -> context.lowerAge ?: return true
            }

        val decision = lowerAge >= minAge
        if (BuildConfig.DEBUG && !decision) {
            Timber.d(
                "FailOpenAudiencePolicy.canView: Blocked item: title=%s, minAge=%d, status=%s, lowerAge=%d",
                label,
                minAge,
                (context as? AudienceContext.Resolved)?.status,
                lowerAge,
            )
        }
        return decision
    }
}
