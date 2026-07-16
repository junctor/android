package com.advice.products.utils

import java.util.Locale

fun Long.toCurrency(prefix: String = "US", showCents: Boolean = false, showPlus: Boolean = false): String {
    val suffix = if (showPlus) "+" else ""

    // if the value is divisible by 100, return without decimals
    if (this % 100 == 0L && !showCents) {
        return "$prefix$${this / 100}$suffix"
    }
    return "$prefix$${String.format(Locale.getDefault(), "%.2f", this / 100f)}$suffix"
}
