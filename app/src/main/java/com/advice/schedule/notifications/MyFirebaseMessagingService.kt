package com.advice.schedule.notifications

import com.google.firebase.messaging.FirebaseMessagingService
import timber.log.Timber

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.e("onNewToken: $token")
    }
}
