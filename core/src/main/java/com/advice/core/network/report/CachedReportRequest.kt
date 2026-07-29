package com.advice.core.network.report

data class CachedReportRequest(
    val endpoint: String,
    val request: ReportRequest,
)
