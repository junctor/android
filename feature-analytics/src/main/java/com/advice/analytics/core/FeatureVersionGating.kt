package com.advice.analytics.core

internal fun isMinVersionEnabled(
    appVersion: Int,
    minVersion: Long,
): Boolean = appVersion == 1 || appVersion >= minVersion
