package com.advice.schedule.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.local.Event
import com.advice.schedule.models.local.Location
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.ui.activities.MainActivity
import com.advice.schedule.utilities.Storage
import com.advice.ui.ScheduleScreenView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class ScheduleFragment : Fragment(), KoinComponent {

    private val storage by inject<Storage>()
    private val viewModel by sharedViewModel<ScheduleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = viewModel.getSchedule().observeAsState().value
                ScheduleScreenView(state) {
                    openEventDetails(it)
                }
            }
        }
    }

    private fun openEventDetails(event: Event) {
        (requireActivity() as MainActivity).showEvent(event)
    }

    companion object {
        private const val EXTRA_TYPE = "type"
        private const val EXTRA_LOCATION = "location"
        private const val EXTRA_SPEAKER = "speaker"

        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }

        fun newInstance(type: FirebaseTag): ScheduleFragment {
            return ScheduleFragment().apply {
                arguments = bundleOf(
                    EXTRA_TYPE to type
                )
            }
        }

        fun newInstance(location: Location): ScheduleFragment {
            return ScheduleFragment().apply {
                arguments = bundleOf(
                    EXTRA_LOCATION to location
                )
            }
        }

        fun newInstance(speaker: Speaker): ScheduleFragment {
            return ScheduleFragment().apply {
                arguments = bundleOf(
                    EXTRA_SPEAKER to speaker
                )
            }
        }
    }
}