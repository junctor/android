package com.advice.schedule.ui.information.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.core.local.Location
import com.advice.core.local.LocationContainer
import com.advice.ui.screens.LocationsScreenView
import com.advice.ui.theme.ScheduleTheme
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class LocationsFragment : Fragment() {

    private val viewModel by sharedViewModel<LocationsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ScheduleTheme {
                    val state =
                        viewModel.state.collectAsState(initial = null).value
                    Timber.e("onCreateView: locations")
                    LocationsScreenView(
                        state?.list ?: emptyList(),
                        { location ->
                            if (location.hasChildren) {
                                viewModel.toggle(location)
                            } else {
//                                openScheduleBottomSheet(location)
                            }
                        },
                        {
                            requireActivity().onBackPressed()
                        })

                    // if (location.hasChildren) {
                    //                viewModel.toggle(location)
                    //            } else {
                    //                openScheduleBottomSheet(location)
                    //            }
                }
            }
        }
    }

    private fun openScheduleBottomSheet(location: Location) {
        Timber.e("openScheduleBottomSheet: $location")
        val fragment = LocationsBottomSheet.newInstance(location)
        fragment.show(childFragmentManager, "location")
    }

    companion object {
        fun newInstance() = LocationsFragment()
    }
}

