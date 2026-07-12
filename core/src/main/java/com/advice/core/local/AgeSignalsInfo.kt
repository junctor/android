package com.advice.core.local

import com.shortstack.core.BuildConfig
import timber.log.Timber
import kotlin.Int

class AgeSignalsInfo(
    val lowerAge: Int?,
    val status: AgeStatus?, // VERIFIED, SUPERVISED, DECLARED, UNKNOWN, ...
)

fun AgeSignalsInfo.canView(minAge: Int?): Boolean {
    val decision = when {
        minAge == null -> true
        lowerAge == null -> true
        else -> lowerAge >= minAge
    }
    if (BuildConfig.DEBUG) {
        Timber.d("AgeSignalsInfo.canView: minAge $minAge, status $status, lowerAge $lowerAge")
        Timber.d("AgeSignalsInfo.canView: decision $decision")
    }
    return decision
}

enum class AgeStatus(val value: Int) {
    Verified(0),
    Supervised(1),
    Declared(5),
    Pending(2),
    Denied(3),
    Unknown(4);

    companion object {
        fun getByValue(value: Int?) = AgeStatus.entries.firstOrNull { it.value == value }
    }
}
