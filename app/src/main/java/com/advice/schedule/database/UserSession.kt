package com.advice.schedule.database

import com.advice.core.local.Conference
import com.advice.schedule.database.datasource.ConferencesDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class UserSession(private val auth: FirebaseAuth = FirebaseAuth.getInstance(), conferencesDataSource: ConferencesDataSource) {

    val user = auth.signInAnonymously().snapshotFlow().map {
        it.user
    }

    val conference = conferencesDataSource.get().map { getConference(-1L, it) }

    private fun getConference(preferred: Long, conferences: List<Conference>): Conference {
        if (preferred != -1L) {
            val pref = conferences.find { it.id == preferred && !it.hasFinished }
            if (pref != null) return pref
        }

        val list = conferences.sortedBy { it.startDate }

        val defcon = list.find { it.code == "DEFCON30" }
        if (defcon?.hasFinished == false) {
            return defcon
        }

        return list.firstOrNull { !it.hasFinished } ?: conferences.last()
    }
}

fun Task<AuthResult>.snapshotFlow(): Flow<AuthResult> = callbackFlow {
    val listenerRegistration = addOnCompleteListener {
        if (!it.isSuccessful) {
            close()
            return@addOnCompleteListener
        }
        if (it.result != null)
            trySend(it.result)
    }
    awaitClose {
        listenerRegistration
    }
}
