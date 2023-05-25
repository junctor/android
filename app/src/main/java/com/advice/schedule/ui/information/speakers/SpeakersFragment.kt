package com.advice.schedule.ui.information.speakers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.schedule.ui.MainActivity
import com.advice.ui.screens.SpeakersScreenView
import com.advice.ui.theme.ScheduleTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SpeakersFragment : Fragment() {

    private val viewModel by viewModel<SpeakersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ScheduleTheme {
                    val state = viewModel.speakers.collectAsState(initial = null).value
                    SpeakersScreenView(state ?: emptyList(), {
                        requireActivity().onBackPressed()
                    }, {
                        (requireActivity() as MainActivity).showSpeaker(it)
                    })
                }
            }
        }
    }

    companion object {
        fun newInstance() = SpeakersFragment()
    }
}