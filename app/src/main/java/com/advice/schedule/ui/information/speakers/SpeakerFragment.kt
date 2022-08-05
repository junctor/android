package com.advice.schedule.ui.information.speakers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.advice.schedule.Response
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.ui.schedule.list.ScheduleAdapter
import com.advice.timehop.StickyRecyclerHeadersDecoration
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentSpeakersBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SpeakerFragment : Fragment() {

    private var _binding: FragmentSpeakersBinding? = null
    private val binding get() = _binding!!

    private val viewModel by sharedViewModel<SpeakersViewModel>()

    private val adapter = ScheduleAdapter()
    private val decoration = StickyRecyclerHeadersDecoration(adapter)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSpeakersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        val speaker = arguments?.getParcelable(EXTRA_SPEAKER) as? Speaker ?: error("speaker must not be null")

        viewModel.getSpeaker(speaker).observe(viewLifecycleOwner) {
            when (it) {
                Response.Init -> {

                }
                Response.Loading -> {

                }
                is Response.Success -> {
                    showSpeaker(it.data)
                }
                is Response.Error -> {

                }
            }
        }

        binding.schedule.adapter = adapter
        binding.schedule.addItemDecoration(decoration)

        viewModel.getSpeakerEvents(speaker).observe(viewLifecycleOwner) { list ->
            binding.eventsHeader.isVisible = list.isNotEmpty()
            adapter.setSchedule(list)
        }
    }

    private fun showSpeaker(speaker: Speaker) = with(binding) {
        speakerName.text = speaker.name

        titleContainer.isVisible = speaker.title.isNotEmpty()
        speakerTitle.text = speaker.title

        toolbar.menu.clear()

        if (speaker.twitter.isNotEmpty()) {
            toolbar.inflateMenu(R.menu.speaker_twitter)
            toolbar.setOnMenuItemClickListener {
                openTwitter(speaker.twitter)
                viewModel.onOpenTwitter(speaker)
                true
            }
        }

        description.isVisible = speaker.description.isNotBlank()
        description.text = speaker.description
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