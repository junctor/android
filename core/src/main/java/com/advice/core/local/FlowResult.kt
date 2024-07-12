package com.advice.core.local

sealed class FlowResult<out T : Any> {
    object Loading : FlowResult<Nothing>()
    data class Success<T : Any>(val value: T) : FlowResult<T>()
    data class Failure(val error: Exception) : FlowResult<Nothing>()

    fun toResultOrNull(): T? {
        return when (this) {
            is Success -> value
            else -> null
        }
    }
}
