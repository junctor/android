package com.advice.schedule.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.advice.core.ui.FiltersScreenState
import com.advice.core.ui.HomeState
import com.advice.locations.LocationsViewModel
import com.advice.locations.ui.LocationsScreenView
import com.advice.products.ProductsViewModel
import com.advice.products.screens.ProductScreen
import com.advice.products.screens.ProductsScreen
import com.advice.products.screens.ProductsSummaryScreen
import com.advice.schedule.ui.home.DismissibleBottomAppBar
import com.advice.schedule.ui.home.HomeViewModel
import com.advice.schedule.ui.information.InformationViewModel
import com.advice.schedule.ui.information.faq.FAQViewModel
import com.advice.schedule.ui.information.info.ConferenceViewModel
import com.advice.schedule.ui.information.speakers.SpeakerViewModel
import com.advice.schedule.ui.information.speakers.SpeakersViewModel
import com.advice.schedule.ui.information.vendors.VendorsViewModel
import com.advice.schedule.ui.schedule.FiltersViewModel
import com.advice.schedule.ui.schedule.ScheduleViewModel
import com.advice.schedule.ui.settings.SettingsViewModel
import com.advice.ui.screens.CodeOfConductScreenView
import com.advice.ui.screens.EventScreenView
import com.advice.ui.screens.FAQScreenView
import com.advice.ui.screens.FilterScreenView
import com.advice.ui.screens.HomeScreenView
import com.advice.ui.screens.InformationScreenView
import com.advice.ui.screens.ScheduleScreenState
import com.advice.ui.screens.ScheduleScreenView
import com.advice.ui.screens.SettingScreenView
import com.advice.ui.screens.SpeakerScreenView
import com.advice.ui.screens.SpeakersScreenView
import com.advice.ui.screens.SupportScreenView
import com.advice.ui.screens.VendorsScreenView
import com.advice.ui.theme.ScheduleTheme
import com.advice.wifi.WifiScreenView
import com.advice.wifi.suggestNetwork
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shortstack.hackertracker.R
import org.koin.core.KoinComponent
import timber.log.Timber

class MainActivity :
    AppCompatActivity(),
    KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {

            // Remember a SystemUiController
            val systemUiController = rememberSystemUiController()
            //val useDarkIcons = MaterialTheme.colors.isLight

            SideEffect {
                // Update all of the system bar colors to be transparent, and use
                // dark icons if the theme is light
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = false
                )
            }

            val navController = rememberNavController()

            ScheduleTheme {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController) }
                    composable("information") { InformationScreen(navController) }
                    composable("event/{id}") { backStackEntry ->
                        EventScreen(navController, backStackEntry.arguments?.getString("id"))
                    }
                    composable("speaker/{id}") { backStackEntry ->
                        SpeakerScreen(navController, backStackEntry.arguments?.getString("id"))
                    }
                    composable("settings") { SettingsScreen(navController) }

                    informationScreens(navController)

                    composable("merch") {
                        MerchScreen(navController)
                    }
                    composable("merch/{id}") { backStackEntry ->
                        MerchItemScreen(navController, backStackEntry.arguments?.getString("id")?.toLong())
                    }
                    composable("merch/summary") {
                        MerchSummary(navController)
                    }
                }
            }
        }
    }

    @Composable
    private fun MerchItemScreen(navController: NavHostController, id: Long?) {
        val viewModel = viewModel<ProductsViewModel>()
        val state = viewModel.state.collectAsState(null).value ?: return
        val merch = state.elements.find { it.id == id } ?: return
        ProductScreen(
            product = merch,
            onAddClicked = {
                viewModel.addToCart(it)
            },
            onBackPressed = {
                navController.popBackStack()
            }
        )
    }

    @Composable
    private fun MerchSummary(navController: NavHostController) {
        val viewModel = viewModel<ProductsViewModel>()
        val state = viewModel.state.collectAsState(null).value ?: return
        ProductsSummaryScreen(
            state = state,
            onQuantityChanged = { id, quantity, variant ->
                viewModel.setQuantity(id, quantity, variant)
            },
            onBackPressed = { navController.popBackStack() },
            onDiscountApplied = {},
        )
    }

    @Composable
    private fun MerchScreen(navController: NavHostController) {
        val viewModel = viewModel<ProductsViewModel>()
        val state = viewModel.state.collectAsState(null).value ?: return
        ProductsScreen(
            state = state,
            onSummaryClicked = {
                navController.navigate("merch/summary")
            },
            onProductClicked = {
                navController.navigate("merch/${it.id}")
            },
        )
    }

    private fun NavGraphBuilder.informationScreens(navController: NavHostController) {
        composable("wifi") { WifiScreen(navController) }
        composable("code_of_conduct") { CodeOfCoductScreen(navController) }
        composable("help_and_support") { SupportScreen(navController) }
        composable("locations") { LocationsScreen(navController) }
        composable("faq") { FAQScreen(navController) }
        composable("partners_and_vendors") { VendorsScreen(navController) }
        composable("speakers") { SpeakersScreen(navController) }
    }

    @Composable
    private fun WifiScreen(navController: NavHostController) {
        WifiScreenView(
            onBackPressed = { navController.popBackStack() },
            onLinkClicked = { suggestNetwork(this) },
        )
    }

    @Composable
    private fun SupportScreen(navController: NavHostController) {
        val viewModel = viewModel<ConferenceViewModel>()
        val state = viewModel.conference.collectAsState(initial = null).value ?: return
        SupportScreenView(
            message = state.support,
            onBackPressed = { navController.popBackStack() },
            onLinkClicked = { /*TODO*/ }
        )
    }

    @Composable
    private fun CodeOfCoductScreen(navController: NavHostController) {
        val viewModel = viewModel<ConferenceViewModel>()
        val state = viewModel.conference.collectAsState(initial = null).value ?: return
        CodeOfConductScreenView(policy = state.conduct) {
            navController.popBackStack()
        }
    }

    @Composable
    private fun LocationsScreen(navController: NavHostController) {
        val viewModel = viewModel<LocationsViewModel>()
        val state = viewModel.state.collectAsState(initial = null).value ?: return
        LocationsScreenView(
            containers = state.list,
            onScheduleClicked = {
                // todo: navController.navigate("location/$it")
            },
            onBackPressed = {
                navController.popBackStack()
            }
        )
    }

    @Composable
    private fun SpeakersScreen(navController: NavHostController) {
        val viewModel = viewModel<SpeakersViewModel>()
        val state = viewModel.speakers.collectAsState(initial = null).value ?: return
        SpeakersScreenView(
            speakers = state,
            onBackPressed = { navController.popBackStack() },
            onSpeakerClicked = { navController.navigate("speaker/$it") },
        )
    }

    @Composable
    private fun VendorsScreen(navController: NavHostController) {
        val viewModel = viewModel<VendorsViewModel>()
        val state = viewModel.vendors.collectAsState(initial = null).value ?: return
        VendorsScreenView(
            vendors = state,
            onBackPressed = {
                navController.popBackStack()
            }
        )
    }

    @Composable
    private fun FAQScreen(navController: NavHostController) {
        val viewModel = viewModel<FAQViewModel>()
        val state = viewModel.faqs.collectAsState(initial = null).value ?: return
        FAQScreenView(
            faqs = state,
            onBackPressed = {
                navController.popBackStack()
            }
        )
    }

    @Composable
    private fun InformationScreen(navController: NavHostController) {
        val viewModel = viewModel<InformationViewModel>()
        val state = viewModel.conference.collectAsState(initial = null).value ?: return
        InformationScreenView(
            hasCodeOfConduct = state.conduct != null,
            hasSupport = state.support != null,
            hasWifi = state.code.contains("DEFCON") || state.code.contains("TEST"),
            onClick = { navController.navigate(it) },
            onBackPressed = {
                navController.popBackStack()
            }
        )
    }

    @Composable
    fun EventScreen(navController: NavHostController, id: String?) {
        // todo: this should be another ViewModel
        val viewModel = viewModel<ScheduleViewModel>()
        val state = viewModel.state.collectAsState(initial = null).value ?: return
        val event = state.days.values.flatten().find { it.id == id!!.toLong() }!!
        EventScreenView(
            event = event,
            onBookmark = { viewModel.bookmark(event) },
            onBackPressed = { navController.popBackStack() },
            onLocationClicked = { navController.navigate("location/${event.location.id}") },
            onSpeakerClicked = { navController.navigate("speaker/${it.id}") }
        )
    }

    @Composable
    fun SpeakerScreen(navController: NavHostController, id: String?) {
        val viewModel = viewModel<SpeakerViewModel>()
        //val state = viewModel.state.collectAsState(initial = null).value ?: return
        SpeakerScreenView(name = "", "", "", emptyList(), onBackPressed = { /*TODO*/ }) { event ->
        }
    }

    @Composable
    fun SettingsScreen(navController: NavHostController) {
        val viewModel = viewModel<SettingsViewModel>()
        val state = viewModel.state.collectAsState(initial = null).value ?: return
        SettingScreenView(
            timeZone = state.timezone,
            version = state.version,
            useConferenceTimeZone = state.useConferenceTimeZone,
            showSchedule = state.showSchedule,
            showFilterButton = state.showFilterButton,
            enableEasterEggs = state.enableEasterEggs,
            enableAnalytics = state.enableAnalytics,
            showTwitterHandle = state.showTwitterHandle,
            onPreferenceChanged = { id, value ->
                viewModel.onPreferenceChanged(id, value)
            },
            onBackPressed = { navController.popBackStack() }
        )
    }

    @Composable
    private fun HomeScreen(navController: NavHostController) {
        Box {
            var isShown by rememberSaveable { mutableStateOf(false) }

            OverlappingPanelsView(
                leftPanel = {
                    val viewModel = viewModel<HomeViewModel>()
                    val state =
                        viewModel.getHomeState().collectAsState(initial = null).value ?: HomeState.Loading
                    HomeScreenView(
                        state = state,
                        onConferenceClick = {
                            viewModel.setConference(it)
                        }, onMerchClick = {
                            navController.navigate("merch")
                        }
                    )
                },
                rightPanel = {
                    val viewModel = viewModel<FiltersViewModel>()
                    val state =
                        viewModel.state.collectAsState(initial = FiltersScreenState.Init).value
                    FilterScreenView(
                        state = state,
                        onClick = {
                            viewModel.toggle(it)
                        }, onClear = {
                            viewModel.clearBookmarks()
                        }
                    )
                },
                mainPanel = {
                    val viewModel = viewModel<ScheduleViewModel>()
                    val state =
                        viewModel.state.collectAsState(initial = ScheduleScreenState.Loading).value
                    ScheduleScreenView(
                        state = state,
                        onMenuClicked = { /*TODO*/ },
                        onSearchQuery = {},
                        onFabClicked = { /*TODO*/ },
                        onEventClick = {
                            navController.navigate("event/${it.id}")
                        },
                        onBookmarkClick = {
                            viewModel.bookmark(it)
                        },
                    )
                },
                onPanelChangedListener = { panel ->
                    isShown = panel == Panel.Left
                }
            )
            DismissibleBottomAppBar(
                Modifier.align(Alignment.BottomCenter),
                isShown = isShown
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(painterResource(id = R.drawable.skull), contentDescription = null)
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painterResource(id = R.drawable.ic_map_white_24dp),
                            contentDescription = null
                        )
                    }
                    IconButton(onClick = { navController.navigate("information") }) {
                        Icon(Icons.Default.Info, contentDescription = null)
                    }
                    IconButton(onClick = { navController.navigate("settings") }) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val target = when {
            intent == null -> null
            intent.hasExtra("target") -> intent.getLongExtra("target", -1L)
            intent.data != null -> {
                intent.data.toString().substringAfter("events/").take(5).toLongOrNull()
            }

            else -> null
        }

        Timber.d("target: $target")
        if (target != null) {
            //todo: navController.navigate("event/$target")
        }
    }

    fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
