package com.advice.retrofit.datasource

import com.advice.core.local.FlowResult
import com.advice.core.local.MapFile
import com.advice.core.local.Maps
import com.advice.core.network.Network
import com.advice.data.session.UserSession
import com.advice.data.sources.MapsDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class RetrofitMapsDataSource(
    userSession: UserSession,
    private val filesDir: File?,
) : MapsDataSource {
    private val _mapsFlow: Flow<FlowResult<Maps>> = userSession.getConferenceFlow().map { state ->
        when (state) {
            FlowResult.Loading -> FlowResult.Loading
            is FlowResult.Failure -> FlowResult.Failure(state.error)
            is FlowResult.Success -> {
                val conference = state.value
                val list = conference.maps.map {
                    val file = File(filesDir, it.filename)
                    if (!file.exists()) {
                        try {
                            val inputStream = downloadFile(it.url)
                            Files.copy(
                                inputStream, file.toPath(), StandardCopyOption.REPLACE_EXISTING
                            )
                        } catch (ex: Exception) {
                            Timber.e("Could not download map: ${ex.message}")
                        }
                    }
                    MapFile(it.name, file)
                }
                FlowResult.Success(Maps(conference, list))
            }
        }
    }.distinctUntilChanged().shareIn(
        CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Lazily,
        replay = 1,
    )

    private suspend fun downloadFile(url: String): InputStream = withContext(Dispatchers.IO) {
        val client = Network.client
        val request = Request.Builder().url(url).build()

        Timber.d("Downloading map from url: $url")

        val response = client.newCall(request).execute()

        Timber.d("Response: $response")

        if (!response.isSuccessful) {
            val message = "Unexpected code $response"
            Timber.e(message)
            throw IOException(message)
        }

        response.body.byteStream()
    }

    override fun get(): Flow<FlowResult<Maps>> = _mapsFlow
}
