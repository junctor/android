package com.advice.retrofit.datasource

import com.advice.core.local.Conference
import com.advice.core.local.ConferenceMap
import com.advice.core.local.FlowResult
import com.advice.core.local.MapFile
import com.advice.core.local.Maps
import com.advice.data.session.UserSession
import com.advice.data.sources.MapsDataSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.concurrent.TimeUnit
import kotlin.time.TimeSource

/** Downloads a remote map PDF into [destination] (parent dirs must already exist). */
fun interface MapFileDownloader {
    suspend fun download(
        url: String,
        destination: File,
    )
}

@OptIn(ExperimentalCoroutinesApi::class)
class RetrofitMapsDataSource(
    userSession: UserSession,
    private val filesDir: File?,
    private val downloader: MapFileDownloader = DefaultMapFileDownloader,
    private val sharingScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) : MapsDataSource,
    Closeable {
    private val mapsFlow: Flow<FlowResult<Maps>> =
        userSession
            .getConferenceFlow()
            .distinctUntilChanged()
            .transformLatest { state ->
                when (state) {
                    FlowResult.Loading -> emit(FlowResult.Loading)
                    is FlowResult.Failure -> emit(FlowResult.Failure(state.error))
                    is FlowResult.Success -> {
                        emitAllMapsForConference(state.value)
                    }
                }
            }.shareIn(
                sharingScope,
                // Prefetch as soon as the datasource is created / conference changes,
                // rather than waiting for the Maps screen to open.
                started = SharingStarted.Eagerly,
                replay = 1,
            )

    private suspend fun FlowCollector<FlowResult<Maps>>.emitAllMapsForConference(conference: Conference) {
        val mark = TimeSource.Monotonic.markNow()
        Timber.d(
            "Maps: conference=%s (%s) mapCount=%d",
            conference.id,
            conference.name,
            conference.maps.size,
        )

        if (conference.maps.isEmpty()) {
            emit(FlowResult.Success(Maps(conference, emptyList())))
            return
        }

        val cacheDir = conferenceCacheDir(conference.id)
        val entries =
            conference.maps.map { map ->
                MapEntry(map, cacheFile(cacheDir, map.filename))
            }

        val completed = linkedMapOf<String, MapFile>()
        val toDownload = mutableListOf<MapEntry>()

        for (entry in entries) {
            if (isValidCache(entry.file)) {
                completed[entry.key] = MapFile(entry.map.name, entry.file)
            } else {
                if (entry.file.exists()) {
                    entry.file.delete()
                }
                toDownload += entry
            }
        }

        fun snapshot(): List<MapFile> = entries.mapNotNull { entry -> completed[entry.key] }

        if (toDownload.isEmpty()) {
            Timber.d(
                "Maps: all %d files cached for conference=%s in %s",
                completed.size,
                conference.id,
                mark.elapsedNow(),
            )
            emit(FlowResult.Success(Maps(conference, snapshot())))
            return
        }

        // Clear stale prior-conference Success/Error immediately when nothing is ready yet.
        if (completed.isEmpty()) {
            emit(FlowResult.Loading)
        } else {
            emit(FlowResult.Success(Maps(conference, snapshot())))
        }

        val updates = Channel<MapFile>(Channel.UNLIMITED)
        val semaphore = Semaphore(MAX_CONCURRENT_DOWNLOADS)

        coroutineScope {
            launch {
                toDownload
                    .map { entry ->
                        async {
                            semaphore.withPermit {
                                downloadEntry(entry, updates)
                            }
                        }
                    }.awaitAll()
                updates.close()
            }

            for (mapFile in updates) {
                completed[mapFile.name] = mapFile
                emit(FlowResult.Success(Maps(conference, snapshot())))
            }
        }

        Timber.d(
            "Maps: finished conference=%s ready=%d/%d in %s",
            conference.id,
            completed.size,
            entries.size,
            mark.elapsedNow(),
        )

        // Final emission covers the case where every download failed (empty list).
        emit(FlowResult.Success(Maps(conference, snapshot())))
    }

    private suspend fun downloadEntry(
        entry: MapEntry,
        updates: Channel<MapFile>,
    ) {
        val mark = TimeSource.Monotonic.markNow()
        try {
            Timber.d("Maps: downloading name=%s url=%s", entry.map.name, entry.map.url)
            downloader.download(entry.map.url, entry.file)
            if (isValidCache(entry.file)) {
                Timber.d(
                    "Maps: downloaded name=%s bytes=%d in %s",
                    entry.map.name,
                    entry.file.length(),
                    mark.elapsedNow(),
                )
                updates.send(MapFile(entry.map.name, entry.file))
            } else {
                Timber.e("Maps: download produced invalid file name=%s", entry.map.name)
                entry.file.delete()
            }
        } catch (ex: CancellationException) {
            entry.file.delete()
            throw ex
        } catch (ex: Exception) {
            Timber.e(ex, "Could not download map: ${entry.map.name}")
            entry.file.delete()
        }
    }

    private fun conferenceCacheDir(conferenceId: Long): File {
        val root = filesDir ?: File(".")
        return File(root, "maps/$conferenceId").also { it.mkdirs() }
    }

    override fun get(): Flow<FlowResult<Maps>> = mapsFlow

    override fun close() {
        sharingScope.cancel()
    }

    private data class MapEntry(
        val map: ConferenceMap,
        val file: File,
    ) {
        val key: String get() = map.name
    }

    companion object {
        private const val MAX_CONCURRENT_DOWNLOADS = 3

        internal fun cacheFile(
            cacheDir: File,
            filename: String,
        ): File {
            val safeName = File(filename).name.ifBlank { "map.pdf" }
            return File(cacheDir, safeName)
        }

        internal fun isValidCache(file: File): Boolean = file.isFile && file.length() > 0L
    }
}

internal object DefaultMapFileDownloader : MapFileDownloader {
    /**
     * Dedicated client for map PDFs. Avoids [Network.client]'s TLS 1.3-only restricted
     * suite, which is poorly suited to CDN downloads and can stall or time out.
     */
    private val client: OkHttpClient by lazy {
        OkHttpClient
            .Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .callTimeout(3, TimeUnit.MINUTES)
            .retryOnConnectionFailure(true)
            .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS, ConnectionSpec.CLEARTEXT))
            .build()
    }

    override suspend fun download(
        url: String,
        destination: File,
    ) = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        val parent = destination.parentFile
        if (parent != null && !parent.exists()) {
            parent.mkdirs()
        }

        val temp = File(destination.parent, "${destination.name}.tmp")
        val mark = TimeSource.Monotonic.markNow()
        try {
            client.newCall(request).execute().use { response ->
                Timber.d(
                    "Maps: response=%s ttfb=%s for %s",
                    response.code,
                    mark.elapsedNow(),
                    url,
                )
                if (!response.isSuccessful) {
                    throw IOException("Unexpected code $response")
                }
                val body = response.body
                val contentLength = body.contentLength()
                body.byteStream().buffered().use { input ->
                    temp.outputStream().buffered().use { output ->
                        input.copyTo(output)
                    }
                }
                Timber.d(
                    "Maps: wrote %d/%d bytes in %s",
                    temp.length(),
                    contentLength,
                    mark.elapsedNow(),
                )
            }
            if (destination.exists()) {
                destination.delete()
            }
            if (!temp.renameTo(destination)) {
                Files.move(
                    temp.toPath(),
                    destination.toPath(),
                    StandardCopyOption.REPLACE_EXISTING,
                )
            }
        } catch (ex: Exception) {
            temp.delete()
            throw ex
        }
    }
}
