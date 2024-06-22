package com.advice.schedule.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.advice.core.ui.FiltersScreenState
import com.advice.core.ui.HomeState
import com.advice.core.ui.ScheduleFilter
import com.advice.documents.presentation.viewmodel.DocumentsViewModel
import com.advice.feedback.ui.screens.FeedbackScreen
import com.advice.locations.presentation.viewmodel.LocationsViewModel
import com.advice.organizations.ui.screens.OrganizationScreen
import com.advice.products.presentation.viewmodel.ProductsViewModel
import com.advice.products.ui.screens.ProductScreen
import com.advice.products.ui.screens.ProductsSummaryScreen
import com.advice.schedule.extensions.navGraphViewModel
import com.advice.schedule.presentation.viewmodel.FAQViewModel
import com.advice.schedule.presentation.viewmodel.FiltersViewModel
import com.advice.schedule.presentation.viewmodel.HomeViewModel
import com.advice.schedule.presentation.viewmodel.MapsViewModel
import com.advice.schedule.presentation.viewmodel.MenuViewModel
import com.advice.schedule.presentation.viewmodel.NewsViewModel
import com.advice.schedule.presentation.viewmodel.OrganizationsViewModel
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.presentation.viewmodel.SearchViewModel
import com.advice.schedule.presentation.viewmodel.SettingsViewModel
import com.advice.schedule.presentation.viewmodel.SpeakerViewModel
import com.advice.schedule.presentation.viewmodel.SpeakersViewModel
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.components.DismissibleBottomAppBar
import com.advice.schedule.ui.components.DragAnchors
import com.advice.schedule.ui.components.OverlappingPanelsView
import com.advice.schedule.ui.screens.SearchScreen
import com.advice.schedule.ui.viewmodels.MainViewModel
import com.advice.ui.screens.EventScreen
import com.advice.ui.screens.FAQScreen
import com.advice.ui.screens.FilterScreen
import com.advice.ui.screens.HomeScreen
import com.advice.ui.screens.ScheduleScreen
import com.advice.ui.screens.SettingScreen
import com.advice.ui.screens.SpeakerScreen
import com.advice.ui.screens.SpeakersScreen
import com.advice.ui.states.MapsScreenState
import com.advice.ui.states.ScheduleScreenState
import com.advice.ui.states.SpeakerState
import com.advice.wifi.suggestNetwork

sealed class Navigation {
    object Home : Navigation()
    object Maps : Navigation()
    data class News(val label: String) : Navigation()
    object Search : Navigation()
    data class Locations(val label: String) : Navigation()
    data class Event(val conference: String, val id: String) : Navigation()
    data class Location(val id: String, val label: String) : Navigation()
    data class Tag(val id: String, val label: String) : Navigation()
    data class Schedule(val label: String, val ids: String) : Navigation()
    data class Content(val label: String) : Navigation()
    data class Speaker(val id: String, val name: String) : Navigation()
    object Settings : Navigation()
    object Wifi : Navigation()
    data class Menu(val label: String, val id: String) : Navigation()
    data class Document(val id: String) : Navigation()
    data class FAQ(val label: String) : Navigation()
    data class Organizations(val label: String, val id: String) : Navigation()
    data class People(val label: String) : Navigation()
    data class Products(val label: String) : Navigation()
    data class Merch(val id: Long) : Navigation()
    data class Organization(val id: Long) : Navigation()
    object Feedback : Navigation()
}

@Composable
internal fun NavHost(navController: NavHostController) {
    val productsViewModel = viewModel<ProductsViewModel>()

    androidx.navigation.compose.NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController)
        }
        composable("maps") {
            MapsScreen(navController)
        }

        composable(
            "news/{label}",
            arguments = listOf(navArgument("label") { type = NavType.StringType }),
        ) { backStackEntry ->
            NewsScreen(navController, backStackEntry.arguments?.getString("label"))
        }
        composable("search") {
            Search(navController)
        }
        composable(
            route = "locations/{label}",
            arguments = listOf(navArgument("label") { type = NavType.StringType })
        ) {
            LocationsScreen(navController)
        }
        composable(
            "event/{conference}/{id}",
            arguments = listOf(navArgument("conference") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            EventScreen(
                navController,
                backStackEntry.arguments?.getString("conference"),
                backStackEntry.arguments?.getString("id")
            )
        }
        composable(
            route = "location/{id}/{label}",
            arguments = listOf(navArgument("id") { type = NavType.StringType },
                navArgument("label") { type = NavType.StringType })
        ) { backStackEntry ->
            LocationScreen(
                navController = navController,
                id = backStackEntry.arguments?.getString("id"),
                label = backStackEntry.arguments?.getString("label")
            )
        }
        composable(
            route = "tag/{id}/{label}",
            arguments = listOf(navArgument("id") { type = NavType.StringType },
                navArgument("label") { type = NavType.StringType })
        ) { backStackEntry ->
            TagScreen(
                navController = navController,
                id = backStackEntry.arguments?.getString("id"),
                label = backStackEntry.arguments?.getString("label")
            )
        }
        composable(
            route = "schedule/{label}/{ids}",
            arguments = listOf(navArgument("label") { type = NavType.StringType },
                navArgument("ids") { type = NavType.StringType }),
        ) {
            TagsScreen(
                navController = navController,
                id = it.arguments?.getString("ids"),
                label = it.arguments?.getString("label"),
            )
        }
        composable(
            "content/{label}",
            arguments = listOf(navArgument("label") { type = NavType.StringType })
        ) {
            ContentListScreen(
                navController = navController,
                label = it.arguments?.getString("label")
            )
        }
        composable(
            "content/{conference}/{id}",
            arguments = listOf(navArgument("conference") { type = NavType.StringType }, navArgument("id") { type = NavType.StringType })
        ) {
            ContentScreen(navController, it.arguments?.getString("conference"), it.arguments?.getString("id"))
        }


        composable(
            route = "speaker/{id}/{name}",
            arguments = listOf(navArgument("id") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }),
        ) { backStackEntry ->
            val context = (LocalContext.current as MainActivity)
            SpeakerScreen(
                navController = navController,
                id = backStackEntry.arguments?.getString("id"),
                name = backStackEntry.arguments?.getString("name"),
                onLinkClicked = {
                    context.openLink(it)
                },
            )
        }
        composable("settings") {
            SettingsScreen(navController)
        }

        composable("wifi") {
            WifiScreen(navController)
        }
        composable(
            "menu/{label}/{id}",
            arguments = listOf(navArgument("label") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            MenuScreen(
                navController = navController,
                id = backStackEntry.arguments?.getString("id"),
                label = backStackEntry.arguments?.getString("label"),
            )
        }
        composable(
            "document/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            DocumentScreen(navController, backStackEntry.arguments?.getString("id"))
        }
        composable(
            "faq/{label}",
            arguments = listOf(
                navArgument("label") { type = NavType.StringType },
            ),
        ) { FAQScreen(navController) }
        composable(
            "organizations/{label}/{id}",
            arguments = listOf(navArgument("label") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType }),
        ) {
            OrganizationsScreen(
                navController = navController,
                id = it.arguments?.getString("id"),
                label = it.arguments?.getString("label"),
            )
        }
        composable(
            "people/{label}",
            arguments = listOf(navArgument("label") { type = NavType.StringType }),
        ) { SpeakersScreen(navController) }

        composable(
            "products/{label}",
            arguments = listOf(navArgument("label") { type = NavType.StringType }),
        ) {
            ProductsScreen(navController, productsViewModel)
        }
        composable(
            "merch/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            ProductScreen(
                navController,
                productsViewModel,
                backStackEntry.arguments?.getString("id")?.toLong()
            )
        }
        composable(
            "merch/summary",
        ) {
            ProductsSummary(navController, productsViewModel)
        }
        composable(
            "organization/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType }),
        ) { backStackEntry ->
            OrganizationScreen(backStackEntry, navController)
        }
        composable(
            "feedback/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            FeedbackScreen(navController)
        }
    }
}

@Composable
private fun OrganizationScreen(
    backStackEntry: NavBackStackEntry, navController: NavHostController
) {
    val context = LocalContext.current

    val id = backStackEntry.arguments?.getString("id")
    val viewModel = navController.navGraphViewModel<OrganizationsViewModel>()

    val flow = remember(id) { viewModel.getOrganization(id?.toLong()) }
    val organization = flow.collectAsState(initial = null).value

    OrganizationScreen(organization = organization, onBackPressed = {
        navController.popBackStack()
    }, onLinkClicked = {
        (context as MainActivity).openLink(it)
    }, onScheduleClicked = { id, label ->
        navController.navigate("schedule/${label}/${id}")
    })
}

@Composable
private fun NewsScreen(navController: NavHostController, label: String?) {
    val viewModel = navController.navGraphViewModel<NewsViewModel>()
    val news = viewModel.getNews().collectAsState(initial = emptyList()).value
    com.advice.ui.screens.NewsScreen(label = label, news = news) {
        navController.popBackStack()
    }
}

@Composable
private fun MapsScreen(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<MapsViewModel>()
    val state = viewModel.state.collectAsState(initial = MapsScreenState.Loading).value
    com.advice.ui.screens.MapsScreen(state = state) {
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
    val product = state.products.find { it.id == id } ?: return
    ProductScreen(product = product, canAdd = state.canAdd, onAddClicked = {
        viewModel.addToCart(it)
        navController.popBackStack()
    }, onBackPressed = {
        navController.popBackStack()
    })
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
        },
        onLearnMore = {
            val merchDocument = state?.merchDocument
            if (merchDocument != null) {
                navController.navigate("document/$merchDocument")
            }
        },
        onDismiss = {
            viewModel.dismiss()
        },
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun WifiScreen(navController: NavHostController) {
    com.advice.wifi.ui.screens.WifiScreen(
        onBackPressed = { navController.popBackStack() },
        onLinkClicked = { suggestNetwork(navController.context) },
    )
}

@Composable
private fun MenuScreen(
    navController: NavHostController,
    label: String?,
    id: String?,
) {
    val viewModel = navController.navGraphViewModel<MenuViewModel>()
    val menus = viewModel.menu.collectAsState(initial = null).value ?: return
    val menu = menus.find { it.id.toString() == id } ?: return

    com.advice.documents.ui.screens.MenuScreen(menu = menu,
        label = label ?: "",
        onNavigationClick = {
            navController.navigate(it)
        },
        onBackPressed = {
            navController.popBackStack()
        })
}

@Composable
private fun DocumentScreen(navController: NavHostController, id: String? = null) {
    val viewModel = navController.navGraphViewModel<DocumentsViewModel>()
    val documents = viewModel.documents.collectAsState(initial = null).value ?: return
    val document = documents.find { it.id == id?.toLong() } ?: return
    com.advice.documents.ui.screens.DocumentScreen(document = document,
        onBackPressed = { navController.popBackStack() })
}

@Composable
private fun LocationsScreen(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<LocationsViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value ?: return
    com.advice.locations.ui.screens.LocationsScreen(containers = state.list, onToggleClicked = {
        viewModel.toggle(it)
    }, onScheduleClicked = {
        // todo: this should URL encode the title
        navController.navigate("location/${it.id}/${it.title.replace("/", "-")}")
    }, onBackPressed = {
        navController.popBackStack()
    })
}

@Composable
private fun SpeakersScreen(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<SpeakersViewModel>()
    val state = viewModel.speakers.collectAsState(initial = null).value
    SpeakersScreen(
        speakers = state,
        onBackPress = { navController.popBackStack() },
        onSpeakerClick = { navController.navigate("speaker/${it.id}/${it.name}") },
    )
}

@Composable
private fun OrganizationsScreen(navController: NavHostController, label: String?, id: String?) {
    val viewModel = navController.navGraphViewModel<OrganizationsViewModel>()
    val state = viewModel.getState(id!!).collectAsState(initial = null).value
    com.advice.organizations.ui.screens.OrganizationsScreen(
        label = label ?: "",
        organizations = state,
        onBackPressed = {
            navController.popBackStack()
        },
        onOrganizationPressed = {
            navController.navigate("organization/${it.id}")
        },
    )
}

@Composable
private fun FAQScreen(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<FAQViewModel>()
    val state = viewModel.faqs.collectAsState(initial = null).value
    FAQScreen(faqs = state, onBackPress = {
        navController.popBackStack()
    })
}

@Composable
private fun Search(navController: NavHostController) {
    val viewModel = navController.navGraphViewModel<SearchViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value
    val conference = viewModel.conference.collectAsState(initial = null).value
    SearchScreen(navController, conference, state, onQueryChanged = {
        viewModel.search(it)
    })
}

@Composable
fun EventScreen(navController: NavHostController, conference: String?, id: String?) {
    val context = LocalContext.current
    // todo: this should be another ViewModel
    val viewModel = navController.navGraphViewModel<ScheduleViewModel>()
    val flow = remember(conference, id) { viewModel.getEvent(conference, id?.toLong()) }
    val event = flow.collectAsState(initial = null).value
    EventScreen(event = event, onBookmark = {
        if (event != null) {
            viewModel.bookmark(event, it)
            (context as MainActivity).requestNotificationPermission()
        }
    }, onBackPressed = { navController.popBackStack() }, onTagClicked = {
        navController.navigate("tag/${it.id}/${it.label}")
    }, onLocationClicked = { location ->
        navController.navigate(
            "location/${location.id}/${
                location.shortName?.replace(
                    "/", "\\"
                )
            }"
        )
    }, onUrlClicked = { url ->
        (context as MainActivity).openLink(url)
    }, onSpeakerClicked = { navController.navigate("speaker/${it.id}/${it.name}") })
}

@Composable
fun SpeakerScreen(
    navController: NavHostController,
    id: String?,
    name: String?,
    onLinkClicked: (String) -> Unit,
) {
    val viewModel = navController.navGraphViewModel<SpeakerViewModel>()
    val speakerDetails by viewModel.speakerDetails.collectAsState(SpeakerState.Loading)

    LaunchedEffect(id) {
        viewModel.fetchSpeakerDetails(id)
    }

    SpeakerScreen(name = name ?: "", state = speakerDetails, onBackPress = {
        navController.popBackStack()
    }, onLinkClick = onLinkClicked, onEventClick = {
        navController.navigate("event/${it.conference}/${it.id}")
    })
}

@Composable
fun LocationScreen(navController: NavHostController, id: String?, label: String?) {
    val context = LocalContext.current
    val viewModel = viewModel<ScheduleViewModel>()
    val state = remember {
        viewModel.getState(ScheduleFilter.Location(id))
    }.collectAsState(initial = ScheduleScreenState.Loading).value

    ScheduleScreen(
        state = state,
        label = label,
        onBackPress = {
            navController.popBackStack()
        },
        onEventClick = {
            navController.navigate("event/${it.conference}/${it.id}")
        },
        onBookmarkClick = { event, isBookmarked ->
            viewModel.bookmark(event, isBookmarked)
            (context as MainActivity).requestNotificationPermission()
        },
    )
}

@Composable
fun TagScreen(navController: NavHostController, id: String?, label: String?) {
    val context = LocalContext.current
    val viewModel = viewModel<ScheduleViewModel>()
    val state = remember {
        viewModel.getState(ScheduleFilter.Tag(id))
    }.collectAsState(initial = ScheduleScreenState.Loading).value

    ScheduleScreen(
        state = state,
        label = label,
        onBackPress = {
            navController.popBackStack()
        },
        onEventClick = {
            navController.navigate("event/${it.conference}/${it.id}")
        },
        onBookmarkClick = { event, isBookmarked ->
            viewModel.bookmark(event, isBookmarked)
            (context as MainActivity).requestNotificationPermission()
        },
    )
}

@Composable
fun TagsScreen(navController: NavHostController, id: String?, label: String?) {
    val context = LocalContext.current
    val viewModel = viewModel<ScheduleViewModel>()
    val state = remember {
        viewModel.getState(ScheduleFilter.Tags(id!!.split(",")))
    }.collectAsState(initial = ScheduleScreenState.Loading).value

    ScheduleScreen(
        state = state,
        label = label,
        onBackPress = {
            navController.popBackStack()
        },
        onEventClick = {
            navController.navigate("event/${it.conference}/${it.id}")
        },
        onBookmarkClick = { event, isBookmarked ->
            viewModel.bookmark(event, isBookmarked)
            (context as MainActivity).requestNotificationPermission()
        },
    )
}

@Composable
fun ContentListScreen(navController: NavHostController, label: String?) {
    val viewModel = navController.navGraphViewModel<ContentViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value ?: return

    com.advice.ui.screens.ContentListScreen(
        state = state,
        label = label,
        onMenuClick = {},
        onContentClick = {
            navController.navigate("content/${it.conference}/${it.id}")
        }
    )
}

@Composable
fun ContentScreen(navController: NavHostController, conference: String?, id: String?) {
    val context = LocalContext.current
    // todo: this should be another ViewModel
    val viewModel = navController.navGraphViewModel<ContentViewModel>()
    val flow = remember(conference, id) { viewModel.getContent(conference, id?.toLong()) }
    val content = flow.collectAsState(initial = null).value

    com.advice.ui.screens.ContentScreen(
        event = content,
        onBookmark = {},
        onBackPressed = { /*TODO*/ },
        onTagClicked = {},
        onUrlClicked = {},
    ) {

    }
}

@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val viewModel = navController.navGraphViewModel<SettingsViewModel>()
    val state = viewModel.state.collectAsState(initial = null).value ?: return
    SettingScreen(timeZone = state.timezone,
        version = state.version,
        useConferenceTimeZone = state.useConferenceTimeZone,
        showSchedule = state.showSchedule,
        showFilterButton = state.showFilterButton,
        enableEasterEggs = state.enableEasterEggs,
        enableAnalytics = state.enableAnalytics,
        showTwitterHandle = state.showTwitterHandle,
        onPreferenceChange = { id, value ->
            viewModel.onPreferenceChanged(id, value)
        },
        onThemeChange = {
            if (viewModel.onThemeChanged(it)) {
                // Recreate the Activity if the theme has changed
                (context as MainActivity).recreate()
            }
        },
        onVersionClick = {
            viewModel.onVersionClick()
            (context as MainActivity).openLink("https://www.youtube.com/watch?v=xvFZjo5PgG0")
        },
        onBackPress = { navController.popBackStack() })
}

@Composable
private fun HomeScreen(navController: NavHostController) {
    val context = LocalContext.current
    val mainViewModel = viewModel<MainViewModel>()
    val viewState by mainViewModel.state.collectAsState()

    val homeViewModel = viewModel<HomeViewModel>()
    val filtersViewModel = viewModel<FiltersViewModel>()
    val scheduleViewModel = viewModel<ScheduleViewModel>()

    val scheduleScreenState = remember {
        scheduleViewModel.getState()
    }.collectAsState(initial = ScheduleScreenState.Loading).value

    val homeState = homeViewModel.getHomeState().collectAsState(initial = HomeState.Loading).value
    val filtersScreenState =
        filtersViewModel.state.collectAsState(initial = FiltersScreenState.Init).value


    Box {
        OverlappingPanelsView(viewState.currentAnchor, leftPanel = {
            HomeScreen(state = homeState, onConferenceClick = {
                homeViewModel.setConference(it)
            }, onNavigationClick = {
                navController.navigate(it)
            }, onDismissNews = {
                homeViewModel.markLatestNewsAsRead(it)
            })
        }, rightPanel = {
            FilterScreen(state = filtersScreenState, onClick = {
                filtersViewModel.toggle(it)
            }, onClear = {
                filtersViewModel.clearBookmarks()
            })
        }, mainPanel = {
            ScheduleScreen(
                state = scheduleScreenState,
                onMenuClick = {
                    mainViewModel.setAnchor(DragAnchors.Start)
                },
                onFabClick = {
                    mainViewModel.setAnchor(DragAnchors.End)
                },
                onEventClick = {
                    navController.navigate("event/${it.conference}/${it.id}")
                },
                onBookmarkClick = { event, isBookmarked ->
                    scheduleViewModel.bookmark(event, isBookmarked)
                    (context as MainActivity).requestNotificationPermission()
                },
            )
        }, onPanelChangedListener = { panel ->
            mainViewModel.setAnchor(panel)
        })
        DismissibleBottomAppBar(
            Modifier.align(Alignment.BottomCenter),
            isShown = viewState.isShown,
        ) {
            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = {
                    mainViewModel.setAnchor(DragAnchors.Center)
                }) {
                    Icon(
                        painterResource(
                            id = com.shortstack.core.R.drawable.logo_glitch
                        ), contentDescription = "Logo"
                    )
                }
                IconButton(onClick = {
                    navController.navigate("maps")
                }) {
                    Icon(
                        painterResource(
                            id = com.shortstack.hackertracker.R.drawable.ic_map_white_24dp
                        ), contentDescription = "Maps"
                    )
                }
                IconButton(onClick = { navController.navigate("search") }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
                IconButton(onClick = { navController.navigate("settings") }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }
        }
    }
}
