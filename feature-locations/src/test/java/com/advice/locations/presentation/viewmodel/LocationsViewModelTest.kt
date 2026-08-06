package com.advice.locations.presentation.viewmodel

import com.advice.core.local.LocationRow
import com.advice.core.local.LocationStatus
import com.advice.locations.data.repositories.LocationsRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
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
class LocationsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val locationsFlow =
        MutableSharedFlow<List<LocationRow>>(replay = 1, extraBufferCapacity = 16)

    private val repository =
        mockk<LocationsRepository>(relaxed = true) {
            every { locations } returns locationsFlow
        }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `state passes through repository locations`() =
        runTest {
            val viewModel = LocationsViewModel(repository)
            val rows = listOf(row(1, isExpanded = true), row(2, isExpanded = false))

            locationsFlow.emit(rows)
            advanceUntilIdle()

            assertEquals(rows, viewModel.state.value.list)
        }

    @Test
    fun `toggling an expanded location collapses it`() =
        runTest {
            val viewModel = LocationsViewModel(repository)

            viewModel.toggle(row(1, isExpanded = true))
            advanceUntilIdle()

            coVerify { repository.collapse(1) }
        }

    @Test
    fun `toggling a collapsed location expands it`() =
        runTest {
            val viewModel = LocationsViewModel(repository)

            viewModel.toggle(row(1, isExpanded = false))
            advanceUntilIdle()

            coVerify { repository.expand(1) }
        }

    private fun row(
        id: Long,
        isExpanded: Boolean,
    ): LocationRow =
        LocationRow(
            id = id,
            title = "Location $id",
            status = LocationStatus.Open,
            depth = 0,
            hasChildren = true,
            isExpanded = isExpanded,
            schedule = emptyList(),
        )
}
