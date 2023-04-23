package com.advice.firebase.datasource

import com.advice.core.local.MapFile
import com.advice.data.UserSession
import com.advice.data.datasource.MapsDataSource
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.io.File

class FirebaseMapsDataSource(
    private val userSession: UserSession,
    private val filesDir: File?,
    private val firebaseStorage: FirebaseStorage,

    ) : MapsDataSource {
    override fun get(): Flow<List<MapFile>> {
        return userSession.getConference().map { conference ->
            conference.maps.map {
                // TODO: Ensure maps will the same name from different conferences don't overlap.
                val file = File(filesDir, it.filename)
                // Downloading the map
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
    }
}