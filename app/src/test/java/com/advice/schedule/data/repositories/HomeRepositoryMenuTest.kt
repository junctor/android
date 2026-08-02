package com.advice.schedule.data.repositories

import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import com.advice.core.local.Menu
import com.advice.core.local.MenuItem
import com.advice.core.local.NewsArticle
import com.advice.core.local.wifi.EapSubject
import com.advice.core.local.wifi.WirelessNetwork
import com.advice.core.storage.UserPreferencesStore
import com.advice.core.ui.HomeState
import com.advice.data.session.UserSession
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.sources.FeedbackDataSource
import com.advice.wifi.data.repositories.WifiNetworkRepository
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

class HomeRepositoryMenuTest {
    private val userSession = mockk<UserSession>()
    private val conferencesDataSource = mockk<ConferencesDataSource>()
    private val menuRepository = mockk<MenuRepository>()
    private val newsRepository = mockk<NewsRepository>()
    private val networkRepository = mockk<WifiNetworkRepository>()
    private val feedbackDataSource = mockk<FeedbackDataSource>()
    private val storage = mockk<UserPreferencesStore>(relaxed = true)
    private val analyticsProvider = mockk<AnalyticsProvider>(relaxed = true)

    @Test
    fun `empty menus returns Menu ERROR`() =
        runTest {
            val conference = Conference.Zero.copy(code = "TEST", homeMenuId = 1)
            stub(
                conference = conference,
                menus = FlowResult.Success(emptyList()),
            )

            val state = subject().contents.first() as HomeState.Loaded

            assertEquals(Menu.ERROR, state.menu)
        }

    @Test
    fun `picks menu matching homeMenuId`() =
        runTest {
            val home = Menu(id = 2, label = "Home", items = emptyList())
            val other = Menu(id = 1, label = "Other", items = emptyList())
            val conference = Conference.Zero.copy(code = "TEST", homeMenuId = 2)
            stub(
                conference = conference,
                menus = FlowResult.Success(listOf(other, home)),
            )

            val state = subject().contents.first() as HomeState.Loaded

            assertEquals(2L, state.menu.id)
            assertEquals("Home", state.menu.label)
        }

    @Test
    fun `injects WiFi menu items when wifi enabled and networks non-empty`() =
        runTest {
            val home = Menu(id = 1, label = "Home", items = emptyList())
            val conference = Conference.Zero.copy(code = "TEST", homeMenuId = 1)
            val network = wirelessNetwork(id = 99, titleText = "DEF CON")
            every { analyticsProvider.isWifiEnabled() } returns true
            stub(
                conference = conference,
                menus = FlowResult.Success(listOf(home)),
                wifi = listOf(network),
            )

            val state = subject().contents.first() as HomeState.Loaded

            val wifiItems = state.menu.items.filterIsInstance<MenuItem.Wifi>()
            assertEquals(1, wifiItems.size)
            assertEquals(99L, wifiItems.single().id)
            assertEquals("Connect to the DEF CON", wifiItems.single().description)
        }

    @Test
    fun `does not inject WiFi when wifi disabled`() =
        runTest {
            val home = Menu(id = 1, label = "Home", items = emptyList())
            val conference = Conference.Zero.copy(code = "TEST", homeMenuId = 1)
            every { analyticsProvider.isWifiEnabled() } returns false
            stub(
                conference = conference,
                menus = FlowResult.Success(listOf(home)),
                wifi = listOf(wirelessNetwork()),
            )

            val state = subject().contents.first() as HomeState.Loaded

            assertTrue(state.menu.items.none { it is MenuItem.Wifi })
        }

    @Test
    fun `unread news when storage has not read news`() =
        runTest {
            val conference = Conference.Zero.copy(code = "TEST", homeMenuId = 1)
            val article =
                NewsArticle(
                    id = 7,
                    name = "Headline",
                    text = "Body",
                    date = Instant.parse("2024-08-01T00:00:00Z"),
                )
            every { storage.hasReadNews("TEST", 7) } returns false
            stub(
                conference = conference,
                menus = FlowResult.Success(listOf(Menu(1, "Home", emptyList()))),
                news = listOf(article),
            )

            val state = subject().contents.first() as HomeState.Loaded

            assertEquals(article, state.news)
        }

    @Test
    fun `news null when already read`() =
        runTest {
            val conference = Conference.Zero.copy(code = "TEST", homeMenuId = 1)
            val article =
                NewsArticle(
                    id = 7,
                    name = "Headline",
                    text = "Body",
                    date = Instant.parse("2024-08-01T00:00:00Z"),
                )
            every { storage.hasReadNews("TEST", 7) } returns true
            stub(
                conference = conference,
                menus = FlowResult.Success(listOf(Menu(1, "Home", emptyList()))),
                news = listOf(article),
            )

            val state = subject().contents.first() as HomeState.Loaded

            assertNull(state.news)
        }

    @Test
    fun `hasChicken only for DEFCON33 with easterEggs and chicken enabled`() =
        runTest {
            val conference = Conference.Zero.copy(code = "DEFCON33", homeMenuId = 1)
            every { storage.easterEggs } returns true
            every { analyticsProvider.isChickenEnabled() } returns true
            stub(
                conference = conference,
                menus = FlowResult.Success(listOf(Menu(1, "Home", emptyList()))),
            )

            val state = subject().contents.first() as HomeState.Loaded

            assertTrue(state.hasChicken)
        }

    @Test
    fun `hasChicken false when not DEFCON33`() =
        runTest {
            val conference = Conference.Zero.copy(code = "DEFCON34", homeMenuId = 1)
            every { storage.easterEggs } returns true
            every { analyticsProvider.isChickenEnabled() } returns true
            stub(
                conference = conference,
                menus = FlowResult.Success(listOf(Menu(1, "Home", emptyList()))),
            )

            val state = subject().contents.first() as HomeState.Loaded

            assertFalse(state.hasChicken)
        }

    private fun subject() =
        HomeRepository(
            userSession,
            conferencesDataSource,
            menuRepository,
            newsRepository,
            networkRepository,
            feedbackDataSource,
            storage,
            analyticsProvider,
        )

    private fun stub(
        conference: Conference,
        menus: FlowResult<List<Menu>>,
        news: List<NewsArticle> = emptyList(),
        wifi: List<WirelessNetwork> = emptyList(),
    ) {
        every { userSession.getConference() } returns flowOf(conference)
        every { conferencesDataSource.get() } returns flowOf(FlowResult.Success(listOf(conference)))
        every { menuRepository.get() } returns flowOf(menus)
        every { newsRepository.get() } returns flowOf(news)
        every { networkRepository.get() } returns flowOf(wifi)
        every { feedbackDataSource.get() } returns flowOf(emptyList())
    }

    private fun wirelessNetwork(
        id: Long = 1,
        titleText: String = "Network",
        networkType: String = "WPA2-Enterprise",
        eapMethod: String? = "PEAP",
        passphrase: String? = null,
    ) = WirelessNetwork(
        anonymousIdentity = null,
        autoJoin = "Y",
        certs = null,
        descriptionText = "",
        disableAssociationMacRandomization = "N",
        disableCaptiveNetworkDetection = "N",
        eapMethod = eapMethod,
        eapSubjects = listOf(EapSubject()),
        enableIpv6 = "Y",
        id = id,
        identity = null,
        isIdentityUserEditable = null,
        isSsidHidden = "N",
        networkType = networkType,
        passphrase = passphrase,
        password = null,
        phase2Method = "MSCHAPV2",
        priority = 0,
        restrictFastLaneQosMarking = "N",
        sortOrder = 0,
        ssid = "ssid",
        titleText = titleText,
        tlsClientCertificateRequired = null,
        tlsClientCertificateSupport = null,
        tlsMaximumVersion = null,
        tlsMinimumVersion = null,
        tlsPreferredVersion = null,
    )
}
