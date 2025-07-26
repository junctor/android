package com.advice.core.utils

import android.widget.Toast

data class ToastData(
    val text: String,
    val duration: Int = Toast.LENGTH_SHORT,
)
