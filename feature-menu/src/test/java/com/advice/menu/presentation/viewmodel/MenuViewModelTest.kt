package com.advice.menu.presentation.viewmodel

import com.advice.core.local.FlowResult
import com.advice.core.local.Menu
import com.advice.menu.data.repositories.MenuRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MenuViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val menusFlow =
        MutableSharedFlow<FlowResult<List<Menu>>>(replay = 1, extraBufferCapacity = 16)

    private val repository =
        mockk<MenuRepository> {
            every { get() } returns menusFlow
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
    fun `state is loading until menus arrive`() =
        runTest {
            val viewModel = MenuViewModel(repository)
            advanceUntilIdle()

            assertTrue(viewModel.state.first() is MenuScreenState.Loading)
        }

    @Test
    fun `success maps to success state with menus`() =
        runTest {
            val viewModel = MenuViewModel(repository)
            val menus = listOf(Menu(1, "Home", emptyList()))

            menusFlow.emit(FlowResult.Success(menus))
            advanceUntilIdle()

            val state = viewModel.state.first { it !is MenuScreenState.Loading }
            assertTrue(state is MenuScreenState.Success)
            assertEquals(menus, (state as MenuScreenState.Success).menu)
        }

    @Test
    fun `failure maps to error state`() =
        runTest {
            val viewModel = MenuViewModel(repository)

            menusFlow.emit(FlowResult.Failure(IllegalStateException("boom")))
            advanceUntilIdle()

            assertTrue(viewModel.state.first { it !is MenuScreenState.Loading } is MenuScreenState.Error)
        }
}
