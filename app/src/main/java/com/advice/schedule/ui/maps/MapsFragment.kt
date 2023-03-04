package com.advice.schedule.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.core.local.MapFile
import com.advice.core.local.Location
import com.advice.ui.screens.MapsScreenView
import com.advice.ui.theme.ScheduleTheme
import timber.log.Timber

class MapsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ScheduleTheme {
                    val maps = /*viewModel.maps.observeAsState().value?.dObj as? List<FirebaseConferenceMap> ?:*/ emptyList<MapFile>()
                    Timber.e("Maps: $maps")
                    MapsScreenView(maps) {
                        requireActivity().onBackPressed()
                    }
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
