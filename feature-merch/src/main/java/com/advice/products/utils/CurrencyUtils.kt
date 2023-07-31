package com.advice.products.utils

fun Long.toCurrency(prefix: String = "US"): String {
    // if the value is divisible by 100, return without decimals
    if (this % 100 == 0L) {
        return "$prefix$${this / 100}"
    }
    return "$prefix$${String.format("%.2f", this / 100f)}"
}