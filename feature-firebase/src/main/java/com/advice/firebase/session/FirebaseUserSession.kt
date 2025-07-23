package com.advice.firebase.session

import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import com.advice.core.local.User
import com.advice.core.utils.Storage
import com.advice.data.session.UserSession
import com.advice.data.sources.ConferencesDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class FirebaseUserSession(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val crashlytics: FirebaseCrashlytics,
    conferencesDataSource: ConferencesDataSource,
    private val preferences: Storage,
) : UserSession {
    private val _user = MutableStateFlow<User?>(null)
    override var user: Flow<User?> = _user

    private val _conference = MutableStateFlow<Conference?>(null)
    private val _conferenceFlow: MutableStateFlow<FlowResult<Conference>> =
        MutableStateFlow(FlowResult.Loading)

    override var isDeveloper: Boolean = false

    init {
        CoroutineScope(Job()).launch {
            _conferenceFlow.value = FlowResult.Loading
            conferencesDataSource.get().collect {
                _conference.value = when (it) {
                    is FlowResult.Failure -> {
                        _conferenceFlow.value = FlowResult.Failure(it.error)
                        null
                    }

                    FlowResult.Loading -> {
                        _conferenceFlow.value = FlowResult.Loading
                        null
                    }

                    is FlowResult.Success -> {
                        val previousConference = _conferenceFlow.value.toResultOrNull()

                        val activeConference = getActiveConference(previousConference, it.value)
                        _conferenceFlow.value = if (activeConference != null) {
                            FlowResult.Success(activeConference)
                        } else {
                            FlowResult.Failure(Exception("Could not load conference"))
                        }
                        activeConference
                    }
                }
            }
        }

        CoroutineScope(Job()).launch {
            try {
                val it = auth.signInAnonymously().await()
                val user = it.user
                if (user != null) {
                    Timber.d("User uid: ${user.uid}")
                    _user.value = User(user.uid)
                } else {
                    crashlytics.log("user cannot be signed in")
                    Timber.e("User could not be signed in")
                }
            } catch (ex: Exception) {
                Timber.e(ex, "Could not sign in anonymously")
            }
        }
    }

    private fun getActiveConference(
        previous: Conference?,
        conferences: List<Conference>
    ): Conference? {
        // if we were already looking at a Conference, load the new data for the current Conference
        if (previous != null) {
            val conference = conferences.find { it.id == previous.id }
            if (conference != null) {
                return conference
            }
        }

        return findAvailableConference(preferences.preferredConference, conferences)
    }

    /**
     * Select an appropriate Conference based on what's currently running, what they have previously selected
     * and if Def Con is available or not.
     */
    private fun findAvailableConference(
        preferred: Long,
        conferences: List<Conference>,
    ): Conference? {
        if (conferences.isEmpty()) {
            Timber.e("Could not load conferences.")
            return null
        }

        if (preferred != -1L) {
            val pref = conferences.find { it.id == preferred && !it.hasFinished }
            if (pref != null) return pref
        }

        val list = conferences.sortedBy { it.start }

        val defcon = list.find { "DEFCON" in it.code && !it.hasFinished }
        if (defcon != null) {
            return defcon
        }

        return list.firstOrNull { !it.hasFinished } ?: conferences.lastOrNull()
    }

    override fun getConference(): Flow<Conference> {
        return _conference.filterNotNull().distinctUntilChanged()
    }

    override fun getConferenceFlow(): Flow<FlowResult<Conference>> {
        return _conferenceFlow
    }

    override fun setConference(conference: Conference) {
        preferences.preferredConference = conference.id
        _conference.value = conference
        _conferenceFlow.value = FlowResult.Success(conference)
    }

    override val currentConference: Conference?
        get() = _conference.value

    override val currentUser: User?
        get() = _user.value
}
