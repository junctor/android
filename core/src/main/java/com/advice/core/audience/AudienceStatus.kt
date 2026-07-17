package com.advice.core.audience

enum class AudienceStatus(
    val value: Int,
) {
    Verified(0),
    Supervised(1),
    Declared(5),
    Pending(2),
    Denied(3),
    Unknown(4),
    ;

    companion object {
        fun getByValue(value: Int?) = entries.firstOrNull { it.value == value }
    }
}
