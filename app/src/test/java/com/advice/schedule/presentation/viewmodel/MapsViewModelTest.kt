package com.advice.schedule.presentation.viewmodel

import com.advice.core.local.Conference
import com.advice.core.local.ConferenceMap
import com.advice.core.local.FlowResult
import com.advice.core.local.MapFile
import com.advice.core.local.Maps
import com.advice.data.sources.MapsDataSource
import com.advice.schedule.data.repositories.MapRepository
import com.advice.ui.states.MapsScreenState
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
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class MapsViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val mapsFlow = MutableSharedFlow<FlowResult<Maps>>(replay = 1, extraBufferCapacity = 16)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `empty maps emits no maps error`() =
        runTest {
            val viewModel = createViewModel()
            mapsFlow.emit(
                FlowResult.Success(
                    Maps(conference(1, "Alpha"), emptyList()),
                ),
            )
            advanceUntilIdle()

            val state = viewModel.state.first { it !is MapsScreenState.Loading }
            assertTrue(state is MapsScreenState.Error)
            assertEquals("No maps for Alpha", (state as MapsScreenState.Error).message)
        }

    @Test
    fun `preserves selected map across refresh for same conference`() =
        runTest {
            val viewModel = createViewModel()
            val fileA = MapFile("Floor 1", File("a.pdf"))
            val fileB = MapFile("Floor 2", File("b.pdf"))

            mapsFlow.emit(FlowResult.Success(Maps(conference(1, "Alpha"), listOf(fileA, fileB))))
            advanceUntilIdle()

            viewModel.onMapChanged("Floor 2")
            assertEquals(
                "Floor 2",
                (viewModel.state.first() as MapsScreenState.Success).file.name,
            )

            mapsFlow.emit(
                FlowResult.Success(
                    Maps(
                        conference(1, "Alpha"),
                        listOf(
                            MapFile("Floor 1", File("a2.pdf")),
                            MapFile("Floor 2", File("b2.pdf")),
                        ),
                    ),
                ),
            )
            advanceUntilIdle()

            val state = viewModel.state.first() as MapsScreenState.Success
            assertEquals("Floor 2", state.file.name)
        }

    @Test
    fun `conference change clears selection and shows loading then new maps`() =
        runTest {
            val viewModel = createViewModel()
            mapsFlow.emit(
                FlowResult.Success(
                    Maps(conference(1, "Alpha"), emptyList()),
                ),
            )
            advanceUntilIdle()
            assertTrue(viewModel.state.first() is MapsScreenState.Error)

            mapsFlow.emit(FlowResult.Loading)
            advanceUntilIdle()
            assertTrue(viewModel.state.first() is MapsScreenState.Loading)

            val file = MapFile("Map", File("m.pdf"))
            mapsFlow.emit(FlowResult.Success(Maps(conference(2, "Beta"), listOf(file))))
            advanceUntilIdle()

            val state = viewModel.state.first() as MapsScreenState.Success
            assertEquals("Map", state.file.name)
        }

    @Test
    fun `conference change resets selected map to first`() =
        runTest {
            val viewModel = createViewModel()
            mapsFlow.emit(
                FlowResult.Success(
                    Maps(
                        conference(1, "Alpha"),
                        listOf(
                            MapFile("A1", File("a1.pdf")),
                            MapFile("A2", File("a2.pdf")),
                        ),
                    ),
                ),
            )
            advanceUntilIdle()
            viewModel.onMapChanged("A2")

            mapsFlow.emit(
                FlowResult.Success(
                    Maps(
                        conference(2, "Beta"),
                        listOf(
                            MapFile("B1", File("b1.pdf")),
                            MapFile("B2", File("b2.pdf")),
                        ),
                    ),
                ),
            )
            advanceUntilIdle()

            val state = viewModel.state.first() as MapsScreenState.Success
            assertEquals("B1", state.file.name)
        }

    private fun createViewModel(): MapsViewModel {
        val dataSource =
            object : MapsDataSource {
                override fun get() = mapsFlow
            }
        return MapsViewModel(MapRepository(dataSource))
    }

    private fun conference(
        id: Long,
        name: String,
    ): Conference =
        Conference.Zero.copy(
            id = id,
            name = name,
            maps = emptyList<ConferenceMap>(),
        )
}
