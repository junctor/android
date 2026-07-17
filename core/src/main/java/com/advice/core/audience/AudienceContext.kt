package com.advice.core.audience

sealed interface AudienceContext {
    data object Unresolved : AudienceContext

    data object Unavailable : AudienceContext

    data class Resolved(
        val lowerAge: Int?,
        val status: AudienceStatus?,
    ) : AudienceContext
}
