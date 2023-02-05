package com.advice.schedule.ui.events

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.advice.feature_ui.EventScreenView
import com.advice.schedule.Response
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.ReminderManager
import com.advice.schedule.getTintedDrawable
import com.advice.schedule.models.local.Event
import com.advice.schedule.models.local.LocationContainer
import com.advice.schedule.models.local.toColour
import com.advice.schedule.ui.activities.MainActivity
import com.advice.schedule.ui.information.locations.LocationsViewModel
import com.advice.schedule.ui.information.locations.toContainer
import com.advice.schedule.ui.information.speakers.SpeakersAdapter
import com.advice.schedule.ui.schedule.ScheduleViewModel
import com.advice.schedule.utilities.Analytics
import com.advice.schedule.utilities.Storage
import com.advice.schedule.utilities.TimeUtil
import com.advice.schedule.utilities.getLocalizedDate
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentEventBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class EventFragment : Fragment() {

    private val analytics by inject<Analytics>()
    private val database by inject<DatabaseManager>()
    private val reminder by inject<ReminderManager>()
    private val storage by inject<Storage>()

    private val viewModel by sharedViewModel<ScheduleViewModel>()
    private val locationsViewModel by sharedViewModel<LocationsViewModel>()

    private lateinit var speakersAdapter: SpeakersAdapter
    private val linksAdapter = EventDetailsAdapter()

    private lateinit var event: Event

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        event = arguments?.getParcelable(EXTRA_EVENT) ?: error("event must not be null")

        return ComposeView(requireContext()).apply {
            setContent {
                EventScreenView()
            }
        }
    }

    private fun onShareClick(event: Event) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, event.title)
            putExtra(Intent.EXTRA_TEXT, "https://info.defcon.org/events/${event.id}/")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun onBookmarkClick(event: Event) {
        event.isBookmarked = !event.isBookmarked

        database.updateBookmark(event)
        if (event.isBookmarked) {
            reminder.setReminder(event)
        } else {
            reminder.cancel(event)
        }

        analytics.onEventBookmark(event)
    }

    companion object {

        const val EXTRA_EVENT = "EXTRA_EVENT"

        fun newInstance(event: Event): EventFragment {
            val fragment = EventFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_EVENT, event)
            fragment.arguments = bundle

            return fragment
        }
    }
}