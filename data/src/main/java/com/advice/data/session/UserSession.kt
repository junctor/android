package com.advice.data.session

import android.app.Activity
import com.advice.core.audience.AudienceContext
import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import kotlinx.coroutines.flow.Flow

interface UserSession {
    var audienceContext: Flow<AudienceContext>

    var isDeveloper: Boolean

    // current state
    val currentAudienceContext: AudienceContext

    val currentConference: Conference?

    fun getConference(): Flow<Conference>

    fun getConferenceFlow(): Flow<FlowResult<Conference>>

    fun setConference(conference: Conference)

    /**
     * Resolves Play Age Signals using [activity] so Play can show the sharing prompt when required.
     * Safe to call multiple times; only the first successful resolve replaces [AudienceContext.Unresolved].
     */
    fun resolveAudienceContext(activity: Activity)
}
