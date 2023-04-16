package com.advice.locations

import android.app.Dialog
import android.content.Context
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import com.advice.core.Navigation
import com.advice.core.local.LocationRow
import com.advice.ui.theme.ScheduleTheme
import com.advice.locations.ui.LocationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*


class LocationsBottomSheet : BottomSheetDialogFragment() {

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)

        val location =
            arguments?.getParcelable<LocationRow>("location") ?: error("location cannot be null")

        val view = ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ScheduleTheme {
                    val schedule = location.schedule.map {
                        "${
                            getTimeStamp(
                                context,
                                parse(it.begin)
                            )
                        }   to   ${getTimeStamp(context, parse(it.end))}"
                    }
                    LocationView(
                        title = location.title,
                        schedule = schedule,
                        onScheduleClicked = {
                            (requireActivity() as Navigation).showSchedule(location.id)
                            dismiss()
                        }, onDismiss = {
                            dismiss()
                        })
                }
            }
        }

        dialog.setContentView(view)
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

//        if (App.instance.storage.forceTimeZone) {
//            val timezone = App.instance.database.conference.value?.timezone ?: "America/Los_Angeles"
//            formatter.timeZone = TimeZone.getTimeZone(timezone)
//        }

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
        fun newInstance(location: LocationRow): LocationsBottomSheet {
            val args = bundleOf("location" to location)
            val fragment = LocationsBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }
}