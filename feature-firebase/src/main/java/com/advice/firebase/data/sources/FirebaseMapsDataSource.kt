package com.advice.firebase.data.sources

import com.advice.core.local.MapFile
import com.advice.data.session.UserSession
import com.advice.data.sources.MapsDataSource
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File

class FirebaseMapsDataSource(
    private val userSession: UserSession,
    private val filesDir: File?,
    private val firebaseStorage: FirebaseStorage,
) : MapsDataSource {
    private val _mapsFlow: Flow<List<MapFile>> =
        userSession.getConference()
            .map { conference ->
                conference.maps.map {
                    val file = File(filesDir, it.filename)
                    if (!file.exists()) {
                        val path = "/${conference.code}/${it.filename}"
                        Timber.e("Loading map: $path")
                        val map = firebaseStorage.reference.child(path)
                        try {
                            map.getFile(file).await()
                        } catch (ex: Exception) {
                            Timber.e("Could not download map: ${ex.message}")
                        }
                    }
                    MapFile(it.name, file)
                }
            }
            .distinctUntilChanged()
            .shareIn(
                CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Lazily,
                replay = 1,
            )

    override fun get(): Flow<List<MapFile>> = _mapsFlow
}
