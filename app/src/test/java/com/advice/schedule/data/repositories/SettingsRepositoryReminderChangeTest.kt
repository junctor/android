package com.advice.schedule.data.repositories

import android.content.Context
import com.advice.core.local.Conference
import com.advice.core.local.ReminderMinutes
import com.advice.core.local.ScheduleDayFormat
import com.advice.core.utils.Storage
import com.advice.data.session.UserSession
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test

class SettingsRepositoryReminderChangeTest {
    private val userSession = mockk<UserSession>()
    private val preferences = mockk<Storage>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)
    private val contentRepository = mockk<ContentRepository>(relaxed = true)

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
                contentRepository,
            )
    }

    @Test
    fun `same sanitized event reminder minutes does not reschedule`() {
        subject.onEventReminderMinutesChanged(ReminderMinutes.DEFAULT)

        verify(exactly = 0) { contentRepository.rescheduleBookmarkedReminders() }
        verify(exactly = 0) { preferences.eventReminderMinutes = any() }
    }

    @Test
    fun `changed event reminder minutes reschedules bookmarked reminders`() {
        subject.onEventReminderMinutesChanged(30)

        verify(exactly = 1) { preferences.eventReminderMinutes = 30 }
        verify(exactly = 1) { contentRepository.rescheduleBookmarkedReminders() }
    }

    @Test
    fun `onScheduleDayFormatChanged sets scheduleDayFormat`() {
        subject.onScheduleDayFormatChanged(ScheduleDayFormat.DayOfWeek.id)

        verify(exactly = 1) { preferences.scheduleDayFormat = ScheduleDayFormat.DayOfWeek }
    }
}
