package com.advice.feedback.network

import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackType
import com.advice.core.network.CachedFeedbackRequest
import com.advice.core.network.Network
import com.advice.core.network.NetworkResponse
import com.advice.core.utils.Storage
import com.advice.feedback.network.models.Feedback
import com.advice.feedback.network.models.FeedbackRequest
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeedbackRepository(
    private val version: String,
    private val storage: Storage,
) {
    suspend fun submitFeedback(contentId: Long, feedback: FeedbackForm): NetworkResponse =
        withContext(Dispatchers.IO) {
            val date = Date()
            val timestamp = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US).format(date)

            val request = FeedbackRequest(
                feedbackFormId = feedback.id,
                contentId = contentId,
                conferenceId = feedback.conference,
                client = "Android $version",
                deviceId = storage.userUUID,
                timestamp = timestamp,
                items = feedback.items.mapNotNull {
                    it.toFeedback()
                })

            val response = submitFeedback(feedback.endpoint, request)
            // cache any failed submissions and retry again later
            if (response is NetworkResponse.Error) {
                storage.storeFeedbackRequest(CachedFeedbackRequest(contentId, feedback))
            }
            return@withContext response
        }

    private suspend fun submitFeedback(url: String, feedback: FeedbackRequest): NetworkResponse =
        withContext(Dispatchers.IO) {
            val gson =
                GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create()

            val json = gson.toJson(feedback)
            val body = json.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder().url(url).post(body).build()

            return@withContext try {
                val response = Network.client.newCall(request).execute()
                Timber.d("Feedback submitted: %s", response.isSuccessful)
                if (response.isSuccessful) {
                    NetworkResponse.Success
                } else {
                    NetworkResponse.Error(Exception(response.message))
                }
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to submit feedback")
                NetworkResponse.Error(ex)
            }
        }

    private fun FeedbackItem.toFeedback(): Feedback? {
        return when (val feedbackType = type) {
            FeedbackType.DisplayOnly -> null
            is FeedbackType.MultiSelect -> {
                Feedback(
                    itemId = id,
                    options = feedbackType.selections.map { it },
                )
            }

            is FeedbackType.SelectOne -> {
                Feedback(
                    itemId = id,
                    options = listOfNotNull(feedbackType.selection),
                )
            }

            is FeedbackType.TextBox -> {
                Feedback(
                    itemId = id,
                    text = feedbackType.value,
                )
            }
        }
    }
}
