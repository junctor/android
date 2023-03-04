package com.advice.schedule.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.advice.core.local.Event
import com.advice.core.local.Speaker
import com.advice.core.local.Tag
import com.advice.core.local.Location
import com.advice.schedule.ui.PanelsFragment
import com.advice.schedule.ui.activities.MainActivity
import com.advice.ui.screens.ScheduleScreenView
import com.advice.ui.theme.ScheduleTheme
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent

class ScheduleFragment : Fragment(), KoinComponent {

    private val viewModel by sharedViewModel<ScheduleViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ScheduleTheme {
                    var state = viewModel.state.collectAsState(initial = null).value

                    val location = arguments?.getParcelable<Location>(EXTRA_LOCATION)
                    
                    if (state != null && location != null) {
                        val entries = state.days.entries
                        val associate = entries.associate { it.key to it.value.filter { it.location.id == location.id } }
                        val days = associate.filter { it.value.isNotEmpty() }
                        state = state.copy(days = days)
                    }

                    ScheduleScreenView(state, {
                        (parentFragment as? PanelsFragment)?.openStartPanel()
                    }, onSearchQuery = { query ->
                        viewModel.search(query)
                    }, {
                        (parentFragment as? PanelsFragment)?.openEndPanel()
                    }, {
                        openEventDetails(it)
                    }, {
                        viewModel.bookmark(it)
                    })
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

        fun newInstance(type: Tag): ScheduleFragment {
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