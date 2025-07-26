package com.advice.core.utils

sealed class Response<out T : Any> {

    data object Init : Response<Nothing>()

    data object Loading : Response<Nothing>()

    data class Success<T : Any>(val data: T) : Response<T>()

    data class Error(val exception: Exception) : Response<Nothing>()
}
