package com.advice.core.network.report

data class ReportRequest(
    val message: String,
    val conferenceId: Long,
    val conferenceName: String,
    val objectType: ReportObjectType,
    val objectId: Long,
    val reportTimestamp: String,
    val reportUuid: String,
    val client: String,
    val deviceIdentifier: String,
)
