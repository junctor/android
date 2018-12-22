package com.shortstack.hackertracker.ui.events

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.shortstack.hackertracker.App
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.analytics.AnalyticsController
import com.shortstack.hackertracker.database.DatabaseManager
import com.shortstack.hackertracker.models.DatabaseEvent
import com.shortstack.hackertracker.models.FirebaseEvent
import com.shortstack.hackertracker.models.FirebaseType
import com.shortstack.hackertracker.models.Speaker
import com.shortstack.hackertracker.ui.activities.MainActivity
import com.shortstack.hackertracker.utils.TimeUtil
import com.shortstack.hackertracker.views.EventView
import com.shortstack.hackertracker.views.SpeakerView
import com.shortstack.hackertracker.views.StatusBarSpacer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.empty_text.*
import kotlinx.android.synthetic.main.fragment_event.*
import javax.inject.Inject


/**
 * Created by Chris on 7/31/2018.
 */
class EventFragment : Fragment() {

    companion object {

        const val EXTRA_EVENT = "EXTRA_EVENT"

        fun newInstance(event: FirebaseEvent): EventFragment {
            val fragment = EventFragment()

            val bundle = Bundle()
            bundle.putParcelable(EXTRA_EVENT, event)
            fragment.arguments = bundle

            return fragment
        }
    }

    @Inject
    lateinit var database: DatabaseManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.clear()
        super.onPrepareOptionsMenu(menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        App.application.component.inject(this)

        val event = arguments?.getParcelable(EXTRA_EVENT) as? FirebaseEvent


        val drawable = ContextCompat.getDrawable(context
                ?: return, R.drawable.ic_arrow_back_white_24dp)
        toolbar.navigationIcon = drawable

        toolbar.setNavigationOnClickListener {
            (activity as? MainActivity)?.popBackStack()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val context = context ?: return
            val height = StatusBarSpacer.getStatusBarHeight(context, app_bar)
            app_bar.setPadding(0, height, 0, 0)
        }

        event?.let {
            AnalyticsController.log("Viewing event ${it.title}")

            collapsing_toolbar.title = it.title

            val body = it.description

            if (body.isNotBlank()) {
                empty.visibility = View.GONE
                description.text = body
            } else {
                empty.visibility = View.VISIBLE
            }

            val url = it.link
            if (url.isNullOrBlank()) {
                link.visibility = View.GONE
            } else {
                link.visibility = View.VISIBLE

                link.setOnClickListener { _ ->
                    onLinkClick(url)
                    AnalyticsController.onEventAction(AnalyticsController.EVENT_OPEN_URL, it)
                }
            }

            share.setOnClickListener { _ ->
                onShareClick(it)
                AnalyticsController.onEventAction(AnalyticsController.EVENT_SHARE, it)
            }

            star.setOnClickListener { _ ->
                onBookmarkClick(it)
            }

            displayDescription(it)

            displayTypes(it)

            displayBookmark(it)


            val speakers = displaySpeakers(it)
//            displayRelatedEvents(it, speakers)

            AnalyticsController.onEventAction(AnalyticsController.EVENT_VIEW, it)
        }
    }

    private fun onLinkClick(url: String?) {
        val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
        context?.startActivity(intent)
    }

    private fun onShareClick(event: FirebaseEvent) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, getDetailsDescription(event))
        intent.type = "text/plain"

        context?.startActivity(intent)
    }

    private fun onBookmarkClick(event: FirebaseEvent) {
//        event.event.isBookmarked = !event.isBookmarked

//        displayBookmark(event)
//
//        Single.fromCallable {
//            database.updateBookmark(event.event)
//        }.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe()

    }

    private fun getDetailsDescription(event: FirebaseEvent): String {
//        val context = context ?:
        return ""

//        return "Attending ${event.title} at ${getFullTimeStamp(context, event)} in ${event.location.firstOrNull()?.name} #hackertracker"
    }

    private fun displayBookmark(event: FirebaseEvent) {

        val context = context ?: return

        val isBookmarked = event.isBookmarked
        val drawable = if (isBookmarked) {
            R.drawable.ic_star_accent_24dp
        } else {
            R.drawable.ic_star_border_white_24dp
        }

        val image = ContextCompat.getDrawable(context, drawable)?.mutate()

        if (isBookmarked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            event.type.firstOrNull()?.let {
//                val color = Color.parseColor(it.color)
//                image?.setTint(color)
//            }
        }

        star.setImageDrawable(image)
    }

    private fun displayDescription(event: FirebaseEvent) {

        val context = context ?: return

        collapsing_toolbar.subtitle = getFullTimeStamp(context, event)
//        location.text = event.location.firstOrNull()?.name ?: "Unknown Location"
    }


    fun getFullTimeStamp(context: Context, event: FirebaseEvent): String {
        val (begin, end) = getTimeStamp(context, event)
        val timestamp = TimeUtil.getRelativeDateStamp(context, event.start)

        return String.format(context.getString(R.string.timestamp_full), timestamp, begin, end)
    }

    fun getTimeStamp(context: Context, event: FirebaseEvent): Pair<String, String> {
        val begin = TimeUtil.getTimeStamp(context, event.start)
        val end = TimeUtil.getTimeStamp(context, event.finish)
        return Pair(begin, end)
    }


    private fun displayTypes(event: FirebaseEvent) {


        database.getTypeForEvent(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { type ->
                    val context = context ?: return@subscribe

                    val color = Color.parseColor(type.color)
                    app_bar.setBackgroundColor(color)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        val drawable = ContextCompat.getDrawable(context, R.drawable.chip_background)?.mutate()
                        drawable?.setTint(color)
                        category_text.background = drawable
                    }

                    category_text.text = type.name
                }
    }

    private fun displaySpeakers(event: FirebaseEvent){
        val context = context ?: return

        database.getSpeakers(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { list ->

                    if(list.isEmpty()) {
                        speakers_header.visibility = View.GONE
                    } else {
                        speakers_header.visibility = View.VISIBLE

                        list.forEach { speaker ->
                            speakers.addView(SpeakerView(context, speaker), ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
                        }
                    }
                }
    }


    private fun displayRelatedEvents(event: FirebaseEvent, speakers: List<Speaker>) {
        val context = context ?: return

//        val relatedEvents = database.getRelatedEvents(event.id, event.types, speakers)
//
//        if (relatedEvents.isNotEmpty()) {
//            related_events_header.visibility = View.VISIBLE
//            relatedEvents.forEach {
//                related_events.addView(EventView(context, it))
//            }
//        } else {
            related_events_header.visibility = View.GONE
//        }
    }
}