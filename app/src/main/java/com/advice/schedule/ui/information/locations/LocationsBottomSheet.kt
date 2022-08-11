package com.advice.schedule.ui.information.locations

import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import com.advice.schedule.App
import com.advice.schedule.models.local.LocationContainer
import com.advice.schedule.ui.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shortstack.hackertracker.R
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


class LocationsBottomSheet : BottomSheetDialogFragment() {

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.bottom_sheet_locations, null)
        dialog.setContentView(view)

        val location = arguments?.getParcelable<LocationContainer>("location") ?: error("location cannot be null")

        Timber.e(location.toString())

        setSchedule(view, location)

        view.findViewById<TextView>(R.id.title).text = location.title

        view.findViewById<View>(R.id.schedule).setOnClickListener {
            (requireActivity() as MainActivity).showSchedule(location.toLocation())
            dismiss()
        }
    }

    private fun setSchedule(view: View, it: LocationContainer) {
        val container = view.findViewById<LinearLayout>(R.id.container)
        container.removeAllViews()

        it.schedule.forEach {
            val view = (layoutInflater.inflate(R.layout.locations_schedule_row, null) as TextView).apply {
                text = "${getTimeStamp(context, parse(it.begin))}   to   ${getTimeStamp(context, parse(it.end))}"
            }
            container.addView(view)
        }
    }

    private fun getTimeStamp(context: Context, date: Date?): String {
        // No start time, return TBA.
        if (date == null)
            return context.getString(R.string.tba)


        val s = if (android.text.format.DateFormat.is24HourFormat(context)) {
            "MMMM d, HH:mm"
        } else {
            "MMMM d, h:mm aa"
        }

        val formatter = SimpleDateFormat(s)

        if (App.instance.storage.forceTimeZone) {
            val timezone = App.instance.database.conference.value?.timezone ?: "America/Los_Angeles"
            formatter.timeZone = TimeZone.getTimeZone(timezone)
        }

        return formatter.format(date)
    }

    private fun parse(date: String): Date? {
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(date)
        } catch (ex: Exception) {
            null
        }
    }


    companion object {
        fun newInstance(location: LocationContainer): LocationsBottomSheet {
            val args = bundleOf("location" to location)
            val fragment = LocationsBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }
}