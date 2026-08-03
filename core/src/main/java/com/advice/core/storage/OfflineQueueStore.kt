package com.advice.core.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.advice.core.network.CachedFeedbackRequest
import com.advice.core.network.CachedReportRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber

class OfflineQueueStore(
    context: Context,
    private val gson: Gson,
) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(UserPreferencesStore.KEY_PREFERENCES, Context.MODE_PRIVATE)

    fun storeFeedbackRequest(cachedFeedbackRequest: CachedFeedbackRequest) {
        val cache = dedupeFeedback(getCachedFeedbackRequest(), cachedFeedbackRequest)
        preferences.edit { putString("cached_feedback_requests", gson.toJson(cache)) }
    }

    fun getCachedFeedbackRequest(): List<CachedFeedbackRequest> {
        val json = preferences.getString("cached_feedback_requests", null) ?: return emptyList()
        try {
            val list =
                gson.fromJson<List<CachedFeedbackRequest>>(
                    json,
                    object : TypeToken<List<CachedFeedbackRequest>>() {}.type,
                )
            return list.orEmpty()
        } catch (ex: Exception) {
            Timber.e("Could not convert stored cached feedback request to list")
            Timber.e(ex)
            return emptyList()
        }
    }

    fun removeCachedFeedbackRequest(request: CachedFeedbackRequest) {
        val cache = getCachedFeedbackRequest().filter { it != request }

        preferences.edit { putString("cached_feedback_requests", gson.toJson(cache)) }
    }

    fun storeReportRequest(cachedReportRequest: CachedReportRequest) {
        val cache = dedupeReport(getCachedReportRequest(), cachedReportRequest)
        preferences.edit { putString("cached_report_requests", gson.toJson(cache)) }
    }

    fun getCachedReportRequest(): List<CachedReportRequest> {
        val json = preferences.getString("cached_report_requests", null) ?: return emptyList()
        try {
            val list =
                gson.fromJson<List<CachedReportRequest>>(
                    json,
                    object : TypeToken<List<CachedReportRequest>>() {}.type,
                ) ?: return emptyList()
            return list.filter { entry ->
                entry.endpoint.isNotBlank() && entry.payloadJson.isNotBlank()
            }
        } catch (ex: Exception) {
            Timber.e("Could not convert stored cached report request to list")
            Timber.e(ex)
            preferences.edit { remove("cached_report_requests") }
            return emptyList()
        }
    }

    fun removeCachedReportRequest(request: CachedReportRequest) {
        val cache = getCachedReportRequest().filter { it != request }

        preferences.edit { putString("cached_report_requests", gson.toJson(cache)) }
    }

    companion object {
        fun feedbackDedupeKey(request: CachedFeedbackRequest): String = "${request.contentId}:${request.feedbackForm.id}"

        fun dedupeFeedback(
            existing: List<CachedFeedbackRequest>,
            incoming: CachedFeedbackRequest,
        ): List<CachedFeedbackRequest> {
            val key = feedbackDedupeKey(incoming)
            return existing.filterNot { feedbackDedupeKey(it) == key } + incoming
        }

        fun reportDedupeKey(request: CachedReportRequest): String = "${request.endpoint}:${request.payloadJson.hashCode()}"

        fun dedupeReport(
            existing: List<CachedReportRequest>,
            incoming: CachedReportRequest,
        ): List<CachedReportRequest> {
            val key = reportDedupeKey(incoming)
            return existing.filterNot { reportDedupeKey(it) == key } + incoming
        }
    }
}
