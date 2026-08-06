package com.advice.organizations.presentation.viewmodel

import com.advice.core.local.Organization
import com.advice.organizations.data.repositories.OrganizationsRepository
import com.advice.organizations.ui.screens.OrganizationScreenState
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrganizationViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val repository = mockk<OrganizationsRepository>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `null id emits error state`() =
        runTest {
            val viewModel = OrganizationViewModel(repository)

            viewModel.getOrganization(id = null)
            advanceUntilIdle()

            assertTrue(viewModel.state.value is OrganizationScreenState.Error)
        }

    @Test
    fun `missing organization emits error state`() =
        runTest {
            coEvery { repository.get(1L) } returns null
            val viewModel = OrganizationViewModel(repository)

            viewModel.getOrganization(id = 1L)
            advanceUntilIdle()

            assertTrue(viewModel.state.value is OrganizationScreenState.Error)
        }

    @Test
    fun `found organization emits success state`() =
        runTest {
            val organization =
                Organization(
                    id = 1,
                    name = "Aerospace Village",
                    description = null,
                    locations = emptyList(),
                    links = emptyList(),
                    media = emptyList(),
                    tag = null,
                    tags = emptyList(),
                )
            coEvery { repository.get(1L) } returns organization
            val viewModel = OrganizationViewModel(repository)

            viewModel.getOrganization(id = 1L)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state is OrganizationScreenState.Success)
            assertEquals(organization, (state as OrganizationScreenState.Success).organization)
        }
}
