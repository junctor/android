package com.advice.schedule.ui.information.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.core.utils.Response
import com.advice.schedule.dObj
import com.advice.schedule.models.local.LocationContainer
import com.advice.ui.screens.LocationsScreenView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentRecyclerviewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

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
                val state = viewModel.getLocations().observeAsState().value
                val containers = state?.dObj as? List<LocationContainer>
                LocationsScreenView(containers ?: emptyList()) {
                    requireActivity().onBackPressed()
                }

                // if (location.hasChildren) {
                //                viewModel.toggle(location)
                //            } else {
                //                openScheduleBottomSheet(location)
                //            }
            }
        }
    }

    private fun openScheduleBottomSheet(location: LocationContainer) {
        val schedule = viewModel.updatedSchedule(location.toLocation())
        schedule.observe(viewLifecycleOwner) {
            schedule.removeObservers(viewLifecycleOwner)
            val fragment = LocationsBottomSheet.newInstance(it)
            fragment.show(childFragmentManager, "location")
        }
    }

    companion object {
        fun newInstance() = LocationsFragment()
    }
}

