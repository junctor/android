package com.advice.schedule.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.advice.core.ui.ScheduleFilter
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
import com.advice.schedule.presentation.viewmodel.SearchViewModel
import com.advice.schedule.presentation.viewmodel.SettingsViewModel
import com.advice.schedule.presentation.viewmodel.SpeakersViewModel
import com.advice.schedule.ui.components.DismissibleBottomAppBar
import com.advice.schedule.ui.components.DragAnchors
import com.advice.schedule.ui.components.OverlappingPanelsView
import com.advice.schedule.ui.screens.SearchScreen
import com.advice.schedule.ui.viewmodels.MainViewModel
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
import com.advice.ui.R

@Composable
internal fun NavHost() {
    val navController = rememberNavController()

    val productsViewModel = viewModel<ProductsViewModel>()

    androidx.navigation.compose.NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("maps") {
            MapsScreen(navController)
        }
        composable("search") {
            Search(navController)
        }
        composable("information") { InformationScreen(navController) }
        composable("event/{id}") { backStackEntry ->
            EventScreen(navController, backStackEntry.arguments?.getString("id"))
        }
        composable("location/{id}") { backStackEntry ->
            LocationScreen(navController, backStackEntry.arguments?.getString("id"))
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
    val state = viewModel.state.collectAsState(null).value
    com.advice.products.ui.screens.ProductsScreen(
        state = state,
        onSummaryClicked = {
            navController.navigate("merch/summary")
        },
        onProductClicked = {
            navController.navigate("merch/${it.id}")
        },
        onBackPressed = {
            navController.popBackStack()
        }
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
        onBackPressed = { navController.popBackStack() }
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
    val state = viewModel.speakers.collectAsState(initial = null).value
    SpeakersScreenView(
        speakers = state,
        onBackPressed = { navController.popBackStack() },
        onSpeakerClicked = { navController.navigate("speaker/${it.id}") },
    )
}

@Composable
private fun VendorsScreen(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<OrganizationsViewModel>()
    val state = viewModel.vendors.collectAsState(initial = null).value
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
    val state = viewModel.villages.collectAsState(initial = null).value
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
    val state = viewModel.faqs.collectAsState(initial = null).value
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
private fun Search(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<SearchViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value
    SearchScreen(state, onQueryChanged = {
        viewModel.search(it)
    }, onBackPressed = {
        viewModel.search("")
        navController.popBackStack()
    })
}

@Composable
fun EventScreen(navController: NavHostController, id: String?) {
    // todo: this should be another ViewModel
    val viewModel = navController.navGraphViewModel<ScheduleViewModel>()
    val state =
        viewModel.getState().collectAsState(initial = null).value as? ScheduleScreenState.Success
            ?: return
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
fun LocationScreen(navController: NavHostController, id: String?) {
    val viewModel = viewModel<ScheduleViewModel>()
    val state =
        viewModel.getState(ScheduleFilter.Location(id))
            .collectAsState(initial = ScheduleScreenState.Loading).value
    ScheduleScreenView(
        state = state,
        onMenuClicked = { /*TODO*/ },
        onFabClicked = { /*TODO*/ },
        onEventClick = {
            navController.navigate("event/${it.id}")
        },
        onBookmarkClick = {
            viewModel.bookmark(it)
        },
    )
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
    val mainViewModel = viewModel<MainViewModel>()
    val viewState by mainViewModel.state.collectAsState()

    val homeViewModel = viewModel<HomeViewModel>()
    val filtersViewModel = viewModel<FiltersViewModel>()
    val scheduleViewModel = viewModel<ScheduleViewModel>()

    val homeState = homeViewModel.getHomeState().collectAsState(initial = HomeState.Loading).value
    val filtersScreenState =
        filtersViewModel.state.collectAsState(initial = FiltersScreenState.Init).value
    val scheduleScreenState =
        scheduleViewModel.getState().collectAsState(initial = ScheduleScreenState.Loading).value

    Box {
        OverlappingPanelsView(
            viewState.currentAnchor,
            leftPanel = {
                HomeScreenView(
                    state = homeState,
                    {
                        homeViewModel.setConference(it)
                    },
                    {
                        navController.navigate(it)
                    })
            },
            rightPanel = {
                FilterScreenView(
                    state = filtersScreenState,
                    onClick = {
                        filtersViewModel.toggle(it)
                    }, onClear = {
                        filtersViewModel.clearBookmarks()
                    }
                )
            },
            mainPanel = {
                ScheduleScreenView(
                    state = scheduleScreenState,
                    onMenuClicked = {
                        mainViewModel.setAnchor(DragAnchors.Start)
                    },
                    onFabClicked = {
                        mainViewModel.setAnchor(DragAnchors.End)
                    },
                    onEventClick = {
                        navController.navigate("event/${it.id}")
                    },
                    onBookmarkClick = {
                        scheduleViewModel.bookmark(it)
                    },
                )
            },
            onPanelChangedListener = { panel ->
                mainViewModel.setAnchor(panel)
            }
        )
        DismissibleBottomAppBar(
            Modifier.align(Alignment.BottomCenter),
            isShown = viewState.isShown,
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
                        painterResource(id = com.shortstack.hackertracker.R.drawable.ic_map_white_24dp),
                        contentDescription = null
                    )
                }
                IconButton(onClick = { navController.navigate("search") }) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            }
        }
    }
}