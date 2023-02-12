package com.advice.schedule.database

import com.advice.core.local.Conference
import com.advice.schedule.database.datasource.ConferencesDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class UserSession(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    conferencesDataSource: ConferencesDataSource,
) {

//    private val _user = MutableStateFlow<FirebaseUser?>(null)
//    val user = _user

    val user = auth.signInAnonymously().snapshotFlow().map {
        it.user
    }

    private val _conference = MutableStateFlow<Conference>(Conference.Zero)
    val conference: Flow<Conference> = _conference

    init {
        CoroutineScope(Job()).launch {
            conferencesDataSource.get().collect {
//                _conference.value = it.random()
                Timber.e("Conference is: ${_conference.value}")
            }
        }

//        auth.signInAnonymously().snapshotFlow().map {
//            Timber.e("AuthResult: ${it.user}")
//            _user.value = it.user
//        }
    }

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

    fun setConference(conference: Conference) {
        _conference.value = conference
    }

    val currentConference: Conference
        get() = _conference.value

    val currentUser: FirebaseUser?
        get() = null
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
