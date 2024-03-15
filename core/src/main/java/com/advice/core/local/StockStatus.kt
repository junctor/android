package com.advice.core.local

enum class StockStatus(val value: String) {
    IN_STOCK("IN"),
    LOW_STOCK("LOW"),
    OUT_OF_STOCK("OUT");

    companion object {
        fun fromString(value: String) = values().find { it.value == value }
    }
}