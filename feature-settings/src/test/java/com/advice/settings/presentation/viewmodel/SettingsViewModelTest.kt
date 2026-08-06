package com.advice.settings.presentation.viewmodel

import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.ReminderMinutes
import com.advice.core.local.ScheduleDayFormat
import com.advice.settings.data.repositories.SettingsRepository
import com.advice.ui.states.SettingsScreenState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val stateFlow =
        MutableSharedFlow<SettingsScreenState>(replay = 1, extraBufferCapacity = 16)

    private val repository =
        mockk<SettingsRepository>(relaxed = true) {
            every { state } returns stateFlow
        }
    private val analytics = mockk<AnalyticsProvider>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `repository state maps to preference list view state`() =
        runTest {
            val viewModel = createViewModel()

            stateFlow.emit(
                SettingsScreenState(
                    timezone = "America/Los_Angeles",
                    version = "1.2.3",
                    useConferenceTimeZone = true,
                    showSchedule = false,
                    showFilterButton = true,
                    enableEasterEggs = true,
                    enableAnalytics = false,
                    enableCrashlytics = false,
                    enableGlitchAnimation = true,
                    scheduleDayFormat = ScheduleDayFormat.DayOfWeek.id,
                    eventReminderMinutes = 15,
                    feedbackReminderMinutes = 0,
                ),
            )
            advanceUntilIdle()

            val state = viewModel.state.value
            assertEquals("America/Los_Angeles", state.timeZone)
            assertEquals("1.2.3", state.version)
            assertTrue(state.enableEasterEggs)
            assertEquals(ScheduleDayFormat.DayOfWeek.id, state.scheduleDayFormat)
            assertEquals(15, state.eventReminderMinutes)
            assertEquals(0, state.feedbackReminderMinutes)
            assertEquals(7, state.preferences.size)

            val timeZonePreference = state.preferences[1]
            assertTrue(timeZonePreference.title.contains("America/Los_Angeles"))
            assertFalse(timeZonePreference.title.contains("{timezone}"))
            assertTrue(timeZonePreference.isChecked)
        }

    @Test
    fun `event reminder minutes are sanitized before delegation`() {
        val viewModel = createViewModel()

        viewModel.onEventReminderMinutesChanged(7)
        viewModel.onEventReminderMinutesChanged(30)

        verify { repository.onEventReminderMinutesChanged(ReminderMinutes.DEFAULT) }
        verify { repository.onEventReminderMinutesChanged(30) }
    }

    @Test
    fun `feedback reminder minutes are sanitized before delegation`() {
        val viewModel = createViewModel()

        viewModel.onFeedbackReminderMinutesChanged(3)
        viewModel.onFeedbackReminderMinutesChanged(0)

        verify { repository.onFeedbackReminderMinutesChanged(ReminderMinutes.DEFAULT) }
        verify { repository.onFeedbackReminderMinutesChanged(0) }
    }

    @Test
    fun `preference and format changes delegate to repository`() {
        val viewModel = createViewModel()

        viewModel.onPreferenceChanged("easter_eggs_enabled", isChecked = true)
        viewModel.onScheduleDayFormatChanged(ScheduleDayFormat.DayOfWeek.id)

        verify { repository.onPreferenceChanged("easter_eggs_enabled", true) }
        verify { repository.onScheduleDayFormatChanged(ScheduleDayFormat.DayOfWeek.id) }
    }

    @Test
    fun `theme change returns repository result`() {
        every { repository.onThemeChanged("dark") } returns true
        val viewModel = createViewModel()

        assertTrue(viewModel.onThemeChanged("dark"))
    }

    @Test
    fun `version click logs analytics`() {
        createViewModel().onVersionClick()

        verify { analytics.onVersionClickEvent() }
    }

    private fun createViewModel(): SettingsViewModel = SettingsViewModel(repository, analytics)
}
