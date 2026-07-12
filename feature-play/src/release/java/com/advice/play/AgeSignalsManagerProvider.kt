package com.advice.play

import android.content.Context
import com.google.android.play.agesignals.AgeSignalsManager
import com.google.android.play.agesignals.AgeSignalsManagerFactory

fun createAgeSignalsManager(context: Context): AgeSignalsManager {
    return AgeSignalsManagerFactory.create(context)
}
