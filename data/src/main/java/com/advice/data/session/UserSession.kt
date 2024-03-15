package com.advice.data.session

import com.advice.core.local.Conference
import com.advice.core.local.User
import kotlinx.coroutines.flow.Flow

interface UserSession {

    var user: Flow<User?>

    var isDeveloper: Boolean

    // current state
    val currentUser: User?

    val currentConference: Conference?

    fun getConference(): Flow<Conference>

    fun setConference(conference: Conference)
}
