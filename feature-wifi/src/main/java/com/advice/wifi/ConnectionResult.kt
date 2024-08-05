package com.advice.wifi

sealed class ConnectionResult {
    data object Success : ConnectionResult()
    data class Error(val message: String) : ConnectionResult()
}
