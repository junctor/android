package com.advice.firebase

import com.advice.core.local.Conference
import com.advice.core.local.User
import com.advice.core.utils.Storage
import com.advice.data.UserSession
import com.advice.data.datasource.ConferencesDataSource
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseUserSession(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    conferencesDataSource: ConferencesDataSource,
    private val preferences: Storage
) : UserSession {

    private val _user = MutableStateFlow<User?>(null)
    override var user: Flow<User?> = _user

    private val _conference = MutableStateFlow<Conference?>(null)

    override var isDeveloper: Boolean = false

    init {
        CoroutineScope(Job()).launch {
            conferencesDataSource.get().collect {
                _conference.value = getConference(preferences.preferredConference, it)
                Timber.e("Conference is: ${_conference.value?.code}")
            }
        }

        CoroutineScope(Job()).launch {
            val it = auth.signInAnonymously().await()
            val user = it.user
            if (user != null) {
                Timber.e("User uid: ${user.uid}")
                _user.value = User(user.uid)
            } else {
                Timber.e("User could not be signed in")
            }
        }
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

    override fun getConference(): Flow<Conference> {
        return _conference.filterNotNull()
    }

    override fun setConference(conference: Conference) {
        preferences.preferredConference = conference.id
        _conference.value = conference
    }

    override val currentConference: Conference?
        get() = _conference.value

    override val currentUser: User?
        get() = _user.value
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
