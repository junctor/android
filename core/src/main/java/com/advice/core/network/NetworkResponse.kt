package com.advice.core.network

sealed class NetworkResponse {
    data object Success : NetworkResponse()
    data class Error(val exception: Exception) : NetworkResponse()
}
