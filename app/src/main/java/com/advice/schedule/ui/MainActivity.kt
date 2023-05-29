package com.advice.schedule.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.advice.core.Navigation
import com.advice.core.local.Event
import com.advice.core.local.Merch
import com.advice.core.local.Speaker
import com.advice.core.local.Tag
import com.advice.core.ui.FiltersScreenState
import com.advice.core.ui.HomeState
import com.advice.merch.MerchFragment
import com.advice.merch.MerchItemFragment
import com.advice.merch.MerchSummaryFragment
import com.advice.schedule.get
import com.advice.schedule.replaceFragment
import com.advice.schedule.ui.events.EventFragment
import com.advice.schedule.ui.home.HomeViewModel
import com.advice.schedule.ui.information.InformationFragment
import com.advice.schedule.ui.information.speakers.SpeakerFragment
import com.advice.schedule.ui.maps.MapsFragment
import com.advice.schedule.ui.schedule.FiltersViewModel
import com.advice.schedule.ui.schedule.ScheduleFragment
import com.advice.schedule.ui.schedule.ScheduleViewModel
import com.advice.schedule.ui.settings.SettingsFragment
import com.advice.schedule.utilities.Analytics
import com.advice.ui.screens.FilterScreenView
import com.advice.ui.screens.HomeScreenView
import com.advice.ui.screens.ScheduleScreenState
import com.advice.ui.screens.ScheduleScreenView
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity :
    AppCompatActivity(),
    NavigationView.OnNavigationItemSelectedListener,
    FragmentManager.OnBackStackChangedListener,
    Navigation,
    KoinComponent {

    private val analytics by inject<Analytics>()

    private lateinit var binding: ActivityMainBinding

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val map = HashMap<Int, Fragment>()

    private var secondaryVisible = false


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


            ScheduleTheme {
                Box() {

                    var isShown by remember { mutableStateOf(false) }

                    OverlappingPanelsView(
                        leftPanel = {
                            val viewModel = viewModel<HomeViewModel>()
                            val state =
                                viewModel.getHomeState().observeAsState().value ?: HomeState.Loading
                            HomeScreenView(state = state, onConferenceClick = {}, onMerchClick = {})
                        },
                        rightPanel = {
                            val viewModel = viewModel<FiltersViewModel>()
                            val state =
                                viewModel.state.collectAsState(initial = FiltersScreenState.Init).value
                            FilterScreenView(state = state, onClick = {}, onClear = {})
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
                                    //showEvent(it)
                                },
                                onBookmarkClick = {},
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
                                Icon(Icons.Default.Call, contentDescription = null)
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(Icons.Default.Call, contentDescription = null)
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(Icons.Default.Call, contentDescription = null)
                            }
                            IconButton(onClick = { /*TODO*/ }) {
                                Icon(Icons.Default.Call, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun DismissibleBottomAppBar(
        modifier: Modifier = Modifier,
        isShown: Boolean,
        content: @Composable RowScope.() -> Unit
    ) {
        var offsetY by remember { mutableStateOf(0.dp) }
        offsetY = if (isShown) 0.dp else 120.dp
        val animatedOffsetY by animateDpAsState(
            targetValue = offsetY,
        )

        BottomAppBar(
            modifier = modifier
                .offset(0.dp, animatedOffsetY)
        ) {
            content()
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
            lifecycleScope.launch {
                val event = TODO()
                if (event != null) {
                    showEvent(event)
                } else {
                    Timber.e("Could not find event by id: $target")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.signInAnonymously().addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Timber.d("Successfully signed in. ${it.result}")
            } else {
                Timber.e("Could not sign in.")
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        setMainFragment(item.itemId, item.title.toString(), false)
        return true
    }

    override fun onBackStackChanged() {
        val fragments = supportFragmentManager.fragments
        val last = fragments.lastOrNull()

        if (last is EventFragment || last is SpeakerFragment) {
            secondaryVisible = true
            binding.main.container.visibility = View.INVISIBLE
        } else {
            secondaryVisible = false
            binding.main.container.visibility = View.VISIBLE

            val panels = fragments.get(PanelsFragment::class.java)
            panels.invalidate()
        }
    }

    private fun getFragment(id: Int): Fragment {
        // TODO: Remove, this is a hacky solution for caching issue with InformationFragment's children fragments.
        if (id == R.id.nav_information)
            return InformationFragment.newInstance()

        if (id == R.id.nav_map)
            return MapsFragment.newInstance()

        if (id == R.id.nav_home)
            return PanelsFragment()

        if (map[id] == null) {
            map[id] = when (id) {
                R.id.nav_map -> MapsFragment.newInstance()
                R.id.nav_settings -> SettingsFragment.newInstance()
                else -> InformationFragment.newInstance()
            }
        }
        return map[id]!!
    }

    fun showEvent(event: Event) {
//        //setAboveFragment(EventFragment.newInstance(event))
        supportFragmentManager.beginTransaction()
            .replace(R.id.container_above, EventFragment.newInstance(event))
            .addToBackStack(null)
            .commit()
        analytics.setScreen(Analytics.SCREEN_EVENT)
    }

    fun showSpeaker(speaker: Speaker?) {
        speaker ?: return
        setAboveFragment(SpeakerFragment.newInstance(speaker))
        analytics.setScreen(Analytics.SCREEN_SPEAKER)
    }

    fun showInformation() {
        setAboveFragment(InformationFragment.newInstance())
        analytics.setScreen(Analytics.SCREEN_INFORMATION)
    }

    fun showMap() {
        setAboveFragment(MapsFragment.newInstance())
        analytics.setScreen(Analytics.SCREEN_MAPS)
    }

    fun showSettings() {
        setAboveFragment(SettingsFragment.newInstance())
        analytics.setScreen(Analytics.SCREEN_SETTINGS)
    }

    override fun showSchedule(location: Long) {
        setAboveFragment(ScheduleFragment.newInstance(location), hasAnimation = false)
        analytics.setScreen(Analytics.SCREEN_SCHEDULE)
    }

    fun showSchedule(type: Tag) {
//        setAboveFragment(ScheduleFragment.newInstance(type), hasAnimation = false)
        analytics.setScreen(Analytics.SCREEN_SCHEDULE)
    }

    fun showSchedule(speaker: Speaker) {
//        setAboveFragment(ScheduleFragment.newInstance(speaker), hasAnimation = false)
        analytics.setScreen(Analytics.SCREEN_SCHEDULE)
    }

    private fun setMainFragment(id: Int, title: String? = null, addToBackStack: Boolean) {
        replaceFragment(getFragment(id), R.id.container, backStack = addToBackStack)

        title?.let {
            supportActionBar?.title = it
        }
    }

    fun setAboveFragment(fragment: Fragment, hasAnimation: Boolean = true) {
        replaceFragment(
            fragment,
            R.id.container_above,
            hasAnimation = hasAnimation
        )
    }

    fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun showMerch() {
        setAboveFragment(MerchFragment.newInstance())
    }

    override fun showMerchItem(merch: Merch) {
        setAboveFragment(MerchItemFragment.newInstance(merch))
    }

    override fun showMerchSummary() {
        setAboveFragment(MerchSummaryFragment.newInstance())
    }
}
