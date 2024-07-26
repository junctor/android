package com.advice.feedback.network

import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackType
import com.advice.core.utils.Storage
import com.advice.feedback.network.models.Feedback
import com.advice.feedback.network.models.FeedbackRequest
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FeedbackRepository(
    private val version: String,
    private val storage: Storage,
) {
    private var logging: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

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

    suspend fun submitFeedback(contentId: Long, feedback: FeedbackForm) =
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
                }
            )

            submitFeedback(feedback.endpoint, request)
        }

    private suspend fun submitFeedback(url: String, feedback: FeedbackRequest) =
        withContext(Dispatchers.IO) {
            val gson = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create()

            val json = gson.toJson(feedback)
            val body = json.toRequestBody("application/json".toMediaTypeOrNull())
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            try {
                val response = client.newCall(request).execute()
                Timber.d("Feedback submitted: %s", response.isSuccessful)
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to submit feedback")
            }
        }
}
