package com.advice.schedule

import com.advice.core.utils.Response

val Response<*>.dObj: Any?
    get() {
        if (this is Response.Success) {
            return this.data
        }
        return null
    }