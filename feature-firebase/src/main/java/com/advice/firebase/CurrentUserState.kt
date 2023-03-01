package com.advice.firebase

import com.advice.core.local.Conference
import com.advice.core.local.User

data class CurrentUserState(val conference: Conference, val user: User?)