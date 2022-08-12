package com.advice.schedule.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.local.Conference
import com.advice.schedule.ui.themes.ThemesManager
import com.advice.schedule.utilities.Analytics
import com.advice.schedule.utilities.Storage
import com.advice.schedule.utilities.Time
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*

class SettingsViewModel : ViewModel(), KoinComponent {

    private val database by inject<DatabaseManager>()
    private val storage by inject<Storage>()
    private val analytics by inject<Analytics>()

    private val conference = database.conference
    private val hasReboot = MutableLiveData<Boolean>()
    private var clickCounter = 0

    private val userId = MutableLiveData<String?>()

    init {
        val calendar = Calendar.getInstance()
        calendar.time = Time.now()

        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        if (dayOfYear >= 219 && storage.getPreference(Storage.EASTER_EGGS_ENABLED_KEY, false)) {
            hasReboot.value = true
        }

        if (storage.getPreference(Storage.DEVELOPER_THEME_UNLOCKED, false)) {
            userId.value = database.user?.uid
        }
    }

    fun setTheme(theme: ThemesManager.Theme) {
        storage.theme = theme
        storage.setPreference(Storage.SAFE_MODE_ENABLED, theme == ThemesManager.Theme.SafeMode)
    }

    fun onVersionClick() {
        if (clickCounter++ == 10) {
            storage.setPreference(Storage.DEVELOPER_THEME_UNLOCKED, true)
            database.updateUserProfileAttribute(mapOf("rank" to "noob"))
            userId.value = database.user?.uid
        }
    }

    fun onDeveloperTwitterClick(isFollow: Boolean) {
        analytics.onDeveloperEvent(isFollow)
    }

    fun getConference(): LiveData<Conference> = conference

    fun hasReboot(): LiveData<Boolean> = hasReboot

    fun shouldDisplayUsername(): LiveData<String?> = userId
}