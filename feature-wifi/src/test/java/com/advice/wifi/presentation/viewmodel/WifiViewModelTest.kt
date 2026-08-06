package com.advice.wifi.presentation.viewmodel

import android.app.Activity
import android.content.Intent
import com.advice.core.local.wifi.WirelessNetwork
import com.advice.wifi.ConnectionResult
import com.advice.wifi.JoinPreparation
import com.advice.wifi.WirelessConnectionManager
import com.advice.wifi.data.repositories.WifiNetworkRepository
import com.advice.wifi.ui.screens.WiFiScreenViewState
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WifiViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    private val manager = mockk<WirelessConnectionManager>()
    private val repository = mockk<WifiNetworkRepository>()

    private val network = mockk<WirelessNetwork>()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `found network emits loaded state`() =
        runTest {
            coEvery { repository.get(1L) } returns network
            val viewModel = createViewModel()

            viewModel.get(1L)
            advanceUntilIdle()

            val state = viewModel.state.value
            assertTrue(state is WiFiScreenViewState.Loaded)
            assertEquals(network, (state as WiFiScreenViewState.Loaded).wirelessNetwork)
        }

    @Test
    fun `missing network emits error state`() =
        runTest {
            coEvery { repository.get(1L) } returns null
            val viewModel = createViewModel()

            viewModel.get(1L)
            advanceUntilIdle()

            assertTrue(viewModel.state.value is WiFiScreenViewState.Error)
        }

    @Test
    fun `save config launches system dialog when platform requires it`() =
        runTest {
            val intent = mockk<Intent>()
            coEvery { manager.prepareJoin(network, false) } returns
                JoinPreparation.LaunchAddNetworks(intent)
            val viewModel = loadedViewModel()
            var launched: Intent? = null

            viewModel.saveWifiConfig { launched = it }
            advanceUntilIdle()

            assertEquals(intent, launched)
        }

    @Test
    fun `save config writes inline join result into state`() =
        runTest {
            coEvery { manager.prepareJoin(network, false) } returns
                JoinPreparation.Completed(ConnectionResult.Suggested)
            val viewModel = loadedViewModel()

            viewModel.saveWifiConfig { }
            advanceUntilIdle()

            val state = viewModel.state.value as WiFiScreenViewState.Loaded
            assertEquals(ConnectionResult.Suggested, state.result)
        }

    @Test
    fun `add networks result codes map to connection results`() =
        runTest {
            val viewModel = loadedViewModel()

            viewModel.onAddNetworksResult(Activity.RESULT_OK)
            assertEquals(
                ConnectionResult.SavedViaSettings,
                (viewModel.state.value as WiFiScreenViewState.Loaded).result,
            )

            viewModel.onAddNetworksResult(Activity.RESULT_CANCELED)
            assertEquals(
                ConnectionResult.Cancelled,
                (viewModel.state.value as WiFiScreenViewState.Loaded).result,
            )

            viewModel.onAddNetworksResult(42)
            assertTrue(
                (viewModel.state.value as WiFiScreenViewState.Loaded).result is ConnectionResult.Error,
            )
        }

    @Test
    fun `force local cert updates loaded state`() =
        runTest {
            val viewModel = loadedViewModel()

            viewModel.forceLocalCert(true)

            assertTrue((viewModel.state.value as WiFiScreenViewState.Loaded).forceLocalCert)
        }

    // Note: disconnect() gates on Build.VERSION.SDK_INT which is 0 on the JVM stub jar,
    // so only the unsupported-version branch is reachable in unit tests.
    @Test
    fun `disconnect reports unsupported on old platforms`() =
        runTest {
            val viewModel = loadedViewModel()

            viewModel.disconnect()
            advanceUntilIdle()

            val result = (viewModel.state.value as WiFiScreenViewState.Loaded).result
            assertTrue(result is ConnectionResult.Error)
        }

    private fun createViewModel(): WifiViewModel = WifiViewModel(manager, repository)

    private fun TestScope.loadedViewModel(): WifiViewModel {
        coEvery { repository.get(1L) } returns network
        val viewModel = createViewModel()
        viewModel.get(1L)
        advanceUntilIdle()
        return viewModel
    }
}
