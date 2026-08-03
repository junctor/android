package com.advice.data.network

sealed class NetworkResponse {
    data object Success : NetworkResponse()

    data class Error(
        val exception: Exception,
    ) : NetworkResponse()
}
