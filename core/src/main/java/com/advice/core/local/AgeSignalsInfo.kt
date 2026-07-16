package com.advice.core.local

import com.shortstack.core.BuildConfig
import timber.log.Timber

class AgeSignalsInfo(
    val lowerAge: Int?,
    val status: AgeStatus?, // VERIFIED, SUPERVISED, DECLARED, UNKNOWN, ...
)

fun AgeSignalsInfo.canView(minAge: Int?, item: Any? = null): Boolean {
    val decision = when {
        minAge == null -> true
        lowerAge == null -> true
        else -> lowerAge >= minAge
    }
    if (BuildConfig.DEBUG && !decision) {
        Timber.d(
            "AgeSignalsInfo.canView: Blocked item: type=${item?.javaClass?.simpleName}, " +
                "minAge=$minAge, status=$status, lowerAge=$lowerAge"
        )
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
