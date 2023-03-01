package com.advice.data

import com.advice.core.local.Conference
import com.advice.core.local.User
import kotlinx.coroutines.flow.Flow

interface UserSession {

    var user: Flow<User?>

    var conference: Flow<Conference>

    var isDeveloper: Boolean


    // current state
    val currentUser: User?

    val currentConference: Conference?

    fun setConference(conference: Conference)
}