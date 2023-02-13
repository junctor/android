package com.advice.schedule.ui.information.speakers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.schedule.dObj
import com.advice.schedule.models.local.Event
import com.advice.schedule.models.local.Speaker
import com.advice.ui.screens.ErrorScreenView
import com.advice.ui.screens.SpeakerScreenView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class SpeakerFragment : Fragment() {

    private val viewModel by sharedViewModel<SpeakersViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val speaker = arguments?.getParcelable(EXTRA_SPEAKER) as? Speaker ?: error("speaker must not be null")

        return ComposeView(requireContext()).apply {
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val events = emptyList<Event>()//viewModel.getSpeakerEvents(speaker).observeAsState().value
                val s = viewModel.getSpeaker(speaker).observeAsState().value?.dObj as? Speaker
                Timber.e("Speaker: $speaker, events: $events, s: $s")
                if(speaker != null) {
                    SpeakerScreenView(speaker.name, speaker.title, speaker.description, events ?: emptyList()) {
                        requireActivity().onBackPressed()
                    }
                } else {
                    ErrorScreenView()
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