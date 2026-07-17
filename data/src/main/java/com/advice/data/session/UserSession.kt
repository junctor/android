package com.advice.data.session

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
}
