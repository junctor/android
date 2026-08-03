package com.advice.firebase.extensions

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import timber.log.Timber

fun <T> QuerySnapshot.toObjectsOrEmpty(clazz: Class<T>): List<T> {
    return try {
        val result = mutableListOf<T>()
        for (d in this) {
            try {
                result.add(d.toObject(clazz))
            } catch (ex: Exception) {
                val path = (this.query as CollectionReference).path + "/${d.id}"
                val message = "Could not map $path to object: ${ex.message}"
                ex.printStackTrace()
                Timber.e(message)
            }
        }
        result
    } catch (ex: Exception) {
        Timber.e("Could not map data to objects: ${ex.message}")
        return emptyList()
    }
}

fun <T> DocumentSnapshot.toObjectOrNull(clazz: Class<T>): T? {
    return try {
        toObject(clazz)
    } catch (ex: Exception) {
        Timber.e("Could not map data to object: ${ex.message}")
        return null
    }
}
