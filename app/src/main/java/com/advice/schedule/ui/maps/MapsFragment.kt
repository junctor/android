package com.advice.schedule.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.core.firebase.FirebaseConferenceMap
import com.advice.schedule.dObj
import com.advice.schedule.models.local.Location
import com.advice.schedule.ui.HackerTrackerViewModel
import com.advice.ui.screens.MapsScreenView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MapsFragment : Fragment() {

    private val viewModel by sharedViewModel<HackerTrackerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val maps = viewModel.maps.observeAsState().value?.dObj as? List<FirebaseConferenceMap> ?: emptyList()
                MapsScreenView(maps) {
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    companion object {

        private const val EXTRA_LOCATION = "location"

        fun newInstance(location: Location? = null): MapsFragment {
            val fragment = MapsFragment()

            if (location != null) {
                val bundle = Bundle()

                bundle.putParcelable(EXTRA_LOCATION, location)
                fragment.arguments = bundle
            }

            return fragment
        }
    }
}
