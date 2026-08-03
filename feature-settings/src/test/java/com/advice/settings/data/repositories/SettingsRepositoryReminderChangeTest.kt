package com.advice.settings.data.repositories

import android.content.Context
import com.advice.core.local.Conference
import com.advice.core.local.ReminderMinutes
import com.advice.core.local.ScheduleDayFormat
import com.advice.data.session.UserSession
import com.advice.data.storage.UserPreferencesStore
import com.advice.settings.BookmarkedReminderRescheduler
import com.advice.settings.TelemetryApplier
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class SettingsRepositoryReminderChangeTest {
    private val userSession = mockk<UserSession>()
    private val preferences = mockk<UserPreferencesStore>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val bookmarkedReminderRescheduler = mockk<BookmarkedReminderRescheduler>(relaxed = true)
    private val telemetryApplier = mockk<TelemetryApplier>(relaxed = true)

    private lateinit var subject: SettingsRepository

    @Before
    fun setUp() {
        every { userSession.getConference() } returns flowOf(Conference.Zero)
        every { preferences.scheduleDayFormatFlow } returns MutableStateFlow(ScheduleDayFormat.MonthDay)
        every { preferences.eventReminderMinutesFlow } returns MutableStateFlow(ReminderMinutes.DEFAULT)
        every { preferences.feedbackReminderMinutesFlow } returns MutableStateFlow(ReminderMinutes.DEFAULT)
        every { preferences.eventReminderMinutes } returns ReminderMinutes.DEFAULT

        subject =
            SettingsRepository(
                userSession,
                preferences,
                version = "1.0",
                context,
                bookmarkedReminderRescheduler,
                telemetryApplier,
            )
    }

    @Test
    fun `same sanitized event reminder minutes does not reschedule`() {
        subject.onEventReminderMinutesChanged(ReminderMinutes.DEFAULT)

        verify(exactly = 0) { bookmarkedReminderRescheduler.reschedule() }
        verify(exactly = 0) { preferences.eventReminderMinutes = any() }
    }

    @Test
    fun `changed event reminder minutes reschedules bookmarked reminders`() {
        subject.onEventReminderMinutesChanged(30)

        verify(exactly = 1) { preferences.eventReminderMinutes = 30 }
        verify(exactly = 1) { bookmarkedReminderRescheduler.reschedule() }
    }

    @Test
    fun `onScheduleDayFormatChanged sets scheduleDayFormat`() {
        subject.onScheduleDayFormatChanged(ScheduleDayFormat.DayOfWeek.id)

        verify(exactly = 1) { preferences.scheduleDayFormat = ScheduleDayFormat.DayOfWeek }
    }
}
