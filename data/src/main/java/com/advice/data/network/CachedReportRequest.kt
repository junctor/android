package com.advice.data.network

/**
 * Cached report submission payload.
 * [payloadJson] is the Gson-serialized HTTP report body.
 */
data class CachedReportRequest(
    val endpoint: String,
    val payloadJson: String,
)
