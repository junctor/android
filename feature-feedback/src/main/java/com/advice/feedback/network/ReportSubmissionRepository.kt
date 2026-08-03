package com.advice.feedback.network

import com.advice.core.local.Conference
import com.advice.data.network.CachedReportRequest
import com.advice.data.network.Network
import com.advice.data.network.NetworkResponse
import com.advice.data.storage.UserPreferencesStore
import com.advice.feedback.BuildConfig
import com.advice.feedback.network.models.ReportObjectType
import com.advice.feedback.network.models.ReportRequest
import com.advice.feedback.storage.OfflineQueueStore
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class ReportSubmissionRepository(
    private val version: String,
    private val preferences: UserPreferencesStore,
    private val offlineQueue: OfflineQueueStore,
) {
    private val gson =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    private val timestampFormatter =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)

    suspend fun submit(
        message: String,
        objectType: ReportObjectType,
        objectId: Long,
        conference: Conference,
    ): NetworkResponse {
        val request =
            ReportRequest(
                message = message.trim(),
                conferenceId = conference.id,
                conferenceName = conference.name,
                objectType = objectType,
                objectId = objectId,
                reportTimestamp =
                    ZonedDateTime
                        .now(ZoneOffset.UTC)
                        .format(timestampFormatter),
                reportUuid =
                    UUID
                        .randomUUID()
                        .toString()
                        .uppercase(Locale.US),
                client = "HackerTracker Android $version",
                deviceIdentifier = preferences.userUUID,
            )

        val endpoint = BuildConfig.REPORT_URL
        val response = post(endpoint, request)
        if (response is NetworkResponse.Error) {
            offlineQueue.storeReportRequest(
                CachedReportRequest(
                    endpoint = endpoint,
                    payloadJson = gson.toJson(request),
                ),
            )
        }
        return response
    }

    suspend fun retryCached() {
        val cached = offlineQueue.getCachedReportRequest()
        for (entry in cached) {
            val endpoint = entry.endpoint
            val payloadJson = entry.payloadJson
            if (endpoint.isBlank() || payloadJson.isBlank()) {
                Timber.w("Dropping invalid cached report request")
                offlineQueue.removeCachedReportRequest(entry)
                continue
            }

            val report =
                try {
                    gson.fromJson(payloadJson, ReportRequest::class.java)
                } catch (ex: Exception) {
                    Timber.e(ex, "Could not deserialize cached report request")
                    offlineQueue.removeCachedReportRequest(entry)
                    continue
                }
            if (report == null) {
                Timber.w("Dropping cached report with empty payload")
                offlineQueue.removeCachedReportRequest(entry)
                continue
            }

            try {
                val response = post(endpoint, report)
                if (response is NetworkResponse.Success) {
                    offlineQueue.removeCachedReportRequest(entry)
                }
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to retry cached report")
            }
        }
    }

    private suspend fun post(
        url: String,
        report: ReportRequest,
    ): NetworkResponse =
        withContext(Dispatchers.IO) {
            val json = gson.toJson(report)
            val body = json.toRequestBody("application/json".toMediaTypeOrNull())
            val request =
                Request
                    .Builder()
                    .url(url)
                    .post(body)
                    .build()

            return@withContext try {
                Network.client.newCall(request).execute().use { response ->
                    Timber.d("Report submitted: %s", response.isSuccessful)
                    if (response.isSuccessful) {
                        NetworkResponse.Success
                    } else {
                        NetworkResponse.Error(Exception(response.message))
                    }
                }
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to submit report")
                NetworkResponse.Error(ex)
            }
        }
}
