package com.advice.schedule.ui.navigation

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.advice.core.ui.FiltersScreenState
import com.advice.core.ui.HomeState
import com.advice.core.ui.InformationState
import com.advice.documents.presentation.viewmodel.DocumentsViewModel
import com.advice.locations.presentation.viewmodel.LocationsViewModel
import com.advice.products.presentation.viewmodel.ProductsViewModel
import com.advice.products.ui.screens.ProductScreen
import com.advice.products.ui.screens.ProductsSummaryScreen
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.FAQViewModel
import com.advice.schedule.presentation.viewmodel.FiltersViewModel
import com.advice.schedule.presentation.viewmodel.HomeViewModel
import com.advice.schedule.presentation.viewmodel.InformationViewModel
import com.advice.schedule.presentation.viewmodel.MapsViewModel
import com.advice.schedule.presentation.viewmodel.OrganizationsViewModel
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.presentation.viewmodel.SettingsViewModel
import com.advice.schedule.presentation.viewmodel.SpeakersViewModel
import com.advice.schedule.ui.components.DismissibleBottomAppBar
import com.advice.schedule.ui.components.OverlappingPanelsView
import com.advice.schedule.ui.components.Panel
import com.advice.ui.screens.EventScreenView
import com.advice.ui.screens.FAQScreenView
import com.advice.ui.screens.FilterScreenView
import com.advice.ui.screens.HomeScreenView
import com.advice.ui.screens.ScheduleScreenState
import com.advice.ui.screens.ScheduleScreenView
import com.advice.ui.screens.SettingScreenView
import com.advice.ui.screens.SpeakerScreenView
import com.advice.ui.screens.SpeakersScreenView
import com.advice.wifi.suggestNetwork
import com.shortstack.hackertracker.R

@Composable
internal fun NavHost() {
    val navController = rememberNavController()

    val productsViewModel = viewModel<ProductsViewModel>()

    androidx.navigation.compose.NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("maps") {
            MapsScreen(navController)
        }
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
            ProductsScreen(navController, productsViewModel)
        }
        composable("merch/{id}") { backStackEntry ->
            ProductScreen(
                navController,
                productsViewModel,
                backStackEntry.arguments?.getString("id")?.toLong()
            )
        }
        composable("merch/summary") {
            ProductsSummary(navController, productsViewModel)
        }
    }
}

@Composable
private fun MapsScreen(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<MapsViewModel>()
    val maps = viewModel.maps.collectAsState(initial = emptyList()).value
    com.advice.ui.screens.MapsScreen(maps = maps) {
        navController.popBackStack()
    }
}

@Composable
private fun ProductScreen(
    navController: NavHostController,
    viewModel: ProductsViewModel,
    id: Long?,
) {
    val state = viewModel.state.collectAsState(null).value ?: return
    val product = state.elements.find { it.id == id } ?: return
    ProductScreen(
        product = product,
        onAddClicked = {
            viewModel.addToCart(it)
            navController.popBackStack()
        },
        onBackPressed = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun ProductsSummary(navController: NavHostController, viewModel: ProductsViewModel) {
    val state = viewModel.state.collectAsState(null).value ?: return
    ProductsSummaryScreen(
        state = state,
        onQuantityChanged = { id, quantity, variant ->
            viewModel.setQuantity(id, quantity, variant)
        },
        onBackPressed = { navController.popBackStack() },
    )
}

@Composable
private fun ProductsScreen(navController: NavHostController, viewModel: ProductsViewModel) {
    val state = viewModel.state.collectAsState(null).value ?: return
    com.advice.products.ui.screens.ProductsScreen(
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
    composable("document/{id}") { backStackEntry ->
        DocumentScreen(navController, backStackEntry.arguments?.getString("id"))
    }
    composable("locations") { LocationsScreen(navController) }
    composable("faq") { FAQScreen(navController) }
    composable("vendors") { VendorsScreen(navController) }
    composable("villages") { VillagesScreen(navController) }
    composable("speakers") { SpeakersScreen(navController) }
}

@Composable
private fun WifiScreen(navController: NavHostController) {
    com.advice.wifi.ui.screens.WifiScreen(
        onBackPressed = { navController.popBackStack() },
        onLinkClicked = { suggestNetwork(navController.context) },
    )
}

@Composable
private fun DocumentScreen(navController: NavHostController, id: String? = null) {
    val viewModel = navController.navGraphViewModel<DocumentsViewModel>()
    val documents = viewModel.documents.collectAsState(initial = null).value ?: return
    val document = documents.find { it.id == id?.toLong() } ?: return
    com.advice.documents.ui.screens.DocumentScreen(
        document = document,
        onBackPressed = { navController.popBackStack() },
        onLinkClicked = { /*TODO*/ }
    )
}

@Composable
private fun LocationsScreen(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<LocationsViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value ?: return
    com.advice.locations.ui.screens.LocationsScreen(
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
    val viewModel = navController.navGraphViewModel<SpeakersViewModel>()
    val state = viewModel.speakers.collectAsState(initial = null).value ?: return
    SpeakersScreenView(
        speakers = state,
        onBackPressed = { navController.popBackStack() },
        onSpeakerClicked = { navController.navigate("speaker/${it.id}") },
    )
}

@Composable
private fun VendorsScreen(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<OrganizationsViewModel>()
    val state = viewModel.vendors.collectAsState(initial = null).value ?: return
    com.advice.organizations.ui.screens.VendorsScreen(
        organizations = state,
        onBackPressed = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun VillagesScreen(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<OrganizationsViewModel>()
    val state = viewModel.villages.collectAsState(initial = null).value ?: return
    com.advice.organizations.ui.screens.VillagesScreen(
        organizations = state,
        onBackPressed = {
            navController.popBackStack()
        }
    )
}

@Composable
private fun FAQScreen(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<FAQViewModel>()
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
    val viewModel = navController.navGraphViewModel<InformationViewModel>()
    val state = viewModel.state.collectAsState(initial = InformationState()).value
    com.advice.ui.screens.InformationScreen(
        state = state,
        onClick = { navController.navigate(it) },
        onBackPressed = {
            navController.popBackStack()
        }
    )
}

@Composable
fun EventScreen(navController: NavHostController, id: String?) {
    // todo: this should be another ViewModel
    val viewModel = navController.navGraphViewModel<ScheduleViewModel>()
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
    val viewModel = navController.navGraphViewModel<SpeakersViewModel>()
    val state =
        viewModel.speakers.collectAsState(initial = null).value?.find { it.id.toString() == id }
            ?: return
    SpeakerScreenView(
        state.name,
        state.title,
        state.description,
        emptyList(),
        onBackPressed = {
            navController.popBackStack()
        }) { event ->
    }
}

@Composable
fun SettingsScreen(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<SettingsViewModel>()
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
                    viewModel.getHomeState().collectAsState(initial = null).value
                        ?: HomeState.Loading
                HomeScreenView(
                    state = state,
                    {
                        viewModel.setConference(it)
                    },
                    {
                        navController.navigate(it)
                    })
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
                IconButton(onClick = {

                }) {
                    Icon(painterResource(id = R.drawable.skull), contentDescription = null)
                }
                IconButton(onClick = {
                    navController.navigate("maps")
                }) {
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