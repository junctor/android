package com.advice.play

import android.content.Context
import com.google.android.play.agesignals.AgeSignalsManager
import com.google.android.play.agesignals.AgeSignalsResult
import com.google.android.play.agesignals.model.AgeSignalsVerificationStatus
import com.google.android.play.agesignals.testing.FakeAgeSignalsManager

/**
 * Supervised 13–17 persona for on-device DEBUG testing.
 * Change [debugPersona] to try a different Age Signals response.
 */
private val debugPersona: AgeSignalsResult =
    AgeSignalsResult.builder()
        .setUserStatus(AgeSignalsVerificationStatus.SUPERVISED)
        .setAgeLower(13)
        .setAgeUpper(17)
        .setInstallId("fake_install_id")
        .build()

fun createAgeSignalsManager(context: Context): AgeSignalsManager {
    return FakeAgeSignalsManager().apply {
        setNextAgeSignalsResult(debugPersona)
    }
}
