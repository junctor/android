package com.advice.core.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow


class ToastManager {

    private val _messages = MutableStateFlow<ToastData?>(null)

    val messages: Flow<ToastData?> = _messages

    fun push(message: ToastData) {
        _messages.value = message
    }

    fun clear() {
        _messages.value = null
    }
}
