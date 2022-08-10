package com.advice.schedule.ui.information.locations

import android.app.Dialog
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.os.bundleOf
import com.advice.schedule.models.local.Location
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shortstack.hackertracker.R
import java.text.SimpleDateFormat

class LocationsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var container: LinearLayout

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.bottom_sheet_review, null)
        dialog.setContentView(view)

        container = view.findViewById<LinearLayout>(R.id.container)
    }



    fun start() {
        val location = arguments!!.getParcelable<Location>("location")!!

        container.removeAllViews()

        location.schedule!!.forEach {

            // todo: Replace with custom View that is easier to read
            val time = if (android.text.format.DateFormat.is24HourFormat(context)) {
                SimpleDateFormat("HH:mm").format(it.begin)
            } else {
                SimpleDateFormat("h:mm aa").format(it.end)
            }

            val view = TextView(context).apply {
                text = time
            }

            container.addView(view)
        }
    }

    companion object {
        fun newInstance(location: Location): LocationsBottomSheet {
            val args = bundleOf("location" to location)
            val fragment = LocationsBottomSheet()
            fragment.arguments = args
            return fragment
        }
    }
}