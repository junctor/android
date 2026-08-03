package com.advice.feedback.network

import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.network.CachedFeedbackRequest
import com.advice.core.network.Network
import com.advice.core.network.NetworkResponse
import com.advice.core.storage.OfflineQueueStore
import com.advice.core.storage.UserPreferencesStore
import com.advice.feedback.network.models.FeedbackRequest
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class FeedbackSubmissionRepository(
    private val version: String,
    private val preferences: UserPreferencesStore,
    private val offlineQueue: OfflineQueueStore,
) {
    private val gson =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    suspend fun submitFeedback(
        contentId: Long?,
        feedback: FeedbackForm,
    ): NetworkResponse =
        withContext(Dispatchers.IO) {
            val request = buildRequest(contentId, feedback)
            val response = post(feedback.endpoint, request)
            // cache any failed submissions and retry again later
            if (response is NetworkResponse.Error) {
                offlineQueue.storeFeedbackRequest(CachedFeedbackRequest(contentId, feedback))
            }
            return@withContext response
        }

    /**
     * Drains the offline feedback queue without re-queuing on failure.
     * Removes entries on success or when corrupt.
     */
    suspend fun retryCached() {
        val cached = offlineQueue.getCachedFeedbackRequest()
        for (entry in cached) {
            val form = entry.feedbackForm
            if (form.endpoint.isBlank()) {
                Timber.w("Dropping cached feedback with blank endpoint")
                offlineQueue.removeCachedFeedbackRequest(entry)
                continue
            }

            try {
                val request = buildRequest(entry.contentId, form)
                val response = post(form.endpoint, request)
                if (response is NetworkResponse.Success) {
                    offlineQueue.removeCachedFeedbackRequest(entry)
                }
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to retry cached feedback")
            }
        }
    }

    private fun buildRequest(
        contentId: Long?,
        feedback: FeedbackForm,
    ): FeedbackRequest {
        val timestamp =
            OffsetDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US),
            )

        return FeedbackRequest(
            feedbackFormId = feedback.id,
            contentId = contentId,
            conferenceId = feedback.conference,
            client = "Android $version",
            deviceId = preferences.userUUID,
            timestamp = timestamp,
            items =
                feedback.items.mapNotNull {
                    it.toFeedback()
                },
        )
    }

    private suspend fun post(
        url: String,
        feedback: FeedbackRequest,
    ): NetworkResponse =
        withContext(Dispatchers.IO) {
            val json = gson.toJson(feedback)
            val body = json.toRequestBody("application/json".toMediaTypeOrNull())
            val request =
                Request
                    .Builder()
                    .url(url)
                    .post(body)
                    .build()

            return@withContext try {
                Network.client.newCall(request).execute().use { response ->
                    Timber.d("Feedback submitted: %s", response.isSuccessful)
                    if (response.isSuccessful) {
                        NetworkResponse.Success
                    } else {
                        NetworkResponse.Error(Exception(response.message))
                    }
                }
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to submit feedback")
                NetworkResponse.Error(ex)
            }
        }
}
