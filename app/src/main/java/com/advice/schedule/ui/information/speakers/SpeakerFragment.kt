package com.advice.schedule.ui.information.speakers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.core.local.Speaker
import com.advice.schedule.ui.MainActivity
import com.advice.ui.screens.SpeakerScreenView
import com.advice.ui.theme.ScheduleTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SpeakerFragment : Fragment() {

    private val viewModel by viewModel<SpeakerViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val speaker = arguments?.getParcelable(EXTRA_SPEAKER) as? Speaker ?: error("speaker must not be null")

        viewModel.setSpeaker(speaker)

        return ComposeView(requireContext()).apply {
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ScheduleTheme {
                    val events = viewModel.events.collectAsState(initial = null).value
                    SpeakerScreenView(speaker.name, speaker.title, speaker.description, events ?: emptyList(), {
                        requireActivity().onBackPressed()
                    }, {
                        (requireActivity() as MainActivity).showEvent(it)
                    })
                }
            }
        }
    }

    private fun openTwitter(url: String) {
        try {
            val url = "https://twitter.com/" + url.replace("@", "")
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
            context?.startActivity(intent)
        } catch (ex: Exception) {
            // do nothing
        }
    }

    companion object {
        private const val EXTRA_SPEAKER = "EXTRA_SPEAKER"

        fun newInstance(speaker: Speaker): SpeakerFragment {
            val fragment = SpeakerFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_SPEAKER, speaker)
            fragment.arguments = bundle

            return fragment
        }
    }
}