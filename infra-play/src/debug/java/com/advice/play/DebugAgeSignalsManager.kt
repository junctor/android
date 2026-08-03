package com.advice.play

import android.content.Context
import com.google.android.play.agesignals.AgeSignalsAccessResult
import com.google.android.play.agesignals.AgeSignalsManager
import com.google.android.play.agesignals.AgeSignalsResult
import com.google.android.play.agesignals.model.AgeRangeSource
import com.google.android.play.agesignals.model.AgeSignalsStatus
import com.google.android.play.agesignals.testing.FakeAgeSignalsManager

/**
 * Supervised 13–17 persona for on-device DEBUG testing.
 * Change [debugPersona] / [debugAccessStatus] to try a different Age Signals response.
 */
private const val debugAccessStatus: Int = AgeSignalsStatus.SHARED

private val debugPersona: AgeSignalsResult =
    AgeSignalsResult
        .builder()
        .setAgeRangeSource(AgeRangeSource.TIER_B)
        .setAgeLower(13)
        .setAgeUpper(17)
        .setInstallId("fake_install_id")
        .build()

fun createAgeSignalsManager(context: Context): AgeSignalsManager =
    FakeAgeSignalsManager().apply {
        setNextAgeSignalsAccessResult(
            AgeSignalsAccessResult
                .builder()
                .setAgeSignalsStatus(debugAccessStatus)
                .build(),
        )
        setNextAgeSignalsResult(debugPersona)
    }
