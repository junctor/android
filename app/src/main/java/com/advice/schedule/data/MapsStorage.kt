package com.advice.schedule.data

import android.content.Context
import android.os.storage.StorageManager
import com.advice.retrofit.datasource.MapFileSpaceAllocator
import com.advice.retrofit.datasource.MapsTelemetry
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import java.io.File
import java.io.FileDescriptor
import java.io.IOException

/**
 * Root directory for the downloaded map cache. Prefers app-specific external storage, but
 * that is null whenever shared storage is not mounted (e.g. corrupted emulated storage).
 * Previously the datasource then fell back to the process working directory — read-only —
 * so every map download failed with "No maps for ..." on affected devices.
 */
fun mapsCacheRoot(
    context: Context,
    crashlytics: FirebaseCrashlytics,
): File {
    val external = context.getExternalFilesDir(null)
    if (external != null) {
        return external
    }
    Timber.e("Maps: external storage unavailable, falling back to internal storage")
    crashlytics.log("Maps: external storage unavailable, falling back to internal storage")
    return context.filesDir
}

/**
 * Reserves disk space via [StorageManager.allocateBytes], which may clear other apps'
 * cached files to satisfy the request and throws [IOException] when it cannot.
 */
class StorageManagerSpaceAllocator(
    private val storageManager: StorageManager,
) : MapFileSpaceAllocator {
    override fun allocate(
        fd: FileDescriptor,
        bytes: Long,
    ) {
        try {
            storageManager.allocateBytes(fd, bytes)
        } catch (ex: IllegalArgumentException) {
            // Volume does not support allocation (e.g. portable SD card); allocation is an
            // optimization there and the write itself will surface any real failure.
            Timber.e(ex, "Maps: storage volume does not support allocateBytes")
        }
    }
}

/**
 * Reports map download failures to Crashlytics as non-fatals, tagged with how many bytes
 * the system could currently make available for the cache — distinguishing "disk full"
 * from network/TLS failures in reports.
 */
class CrashlyticsMapsTelemetry(
    private val cacheRoot: File,
    private val storageManager: StorageManager,
    private val crashlytics: FirebaseCrashlytics,
) : MapsTelemetry {
    override fun report(
        message: String,
        error: Throwable?,
    ) {
        runCatching {
            val uuid = storageManager.getUuidForPath(cacheRoot)
            crashlytics.setCustomKey("maps_allocatable_bytes", storageManager.getAllocatableBytes(uuid))
        }.onFailure { Timber.e(it, "Maps: could not read allocatable bytes") }
        crashlytics.log(message)
        crashlytics.recordException(error ?: IOException(message))
    }
}
