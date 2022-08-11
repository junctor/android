package com.advice.schedule.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.advice.schedule.App
import com.advice.schedule.getTintedDrawable
import com.advice.schedule.models.local.LocationContainer
import com.advice.schedule.models.local.LocationStatus
import com.advice.schedule.models.local.toColour
import com.advice.schedule.toPx
import com.advice.schedule.utilities.Time
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.LocationViewBinding
import java.text.SimpleDateFormat
import java.util.*

class LocationView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private val binding = LocationViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setLocation(location: LocationContainer, useShortLabel: Boolean = false, showDot: Boolean = true) {
        binding.title.text = if (useShortLabel) {
            location.shortTitle ?: location.title
        } else {
            location.title
        }

        val margin = location.depth * 16.toPx

        val layoutParams = binding.status.layoutParams as LayoutParams
        layoutParams.marginStart = margin
        binding.status.layoutParams = layoutParams

        binding.status.isVisible = showDot
        if (showDot) {
            val drawable = context.getTintedDrawable(R.drawable.chip_background, location.status.toColour())
            binding.status.background = drawable
        }

        when (location.status) {
            LocationStatus.Mixed -> {
                // check if has it's own schedule?
                binding.time.isVisible = false
            }
            LocationStatus.Open -> {
                val now = Time.now()
                val timeZone = location.schedule.firstOrNull {
                    val begin = parse(it.begin)
                    val end = parse(it.end)
                    begin != null && end != null && begin.before(now) && end.after(now)
                }
                binding.time.isVisible = timeZone != null
                if (timeZone != null) {
                    val end = getTimeStamp(context, parse(timeZone.end))
                    binding.time.text = "Open until " + end
                }
            }
            LocationStatus.Closed -> {
                val now = Time.now()
                val timeZone = location.schedule.firstOrNull {
                    val begin = parse(it.begin)
                    val end = parse(it.end)
                    begin != null && end != null && begin.after(now)
                    true
                }
                binding.time.isVisible = timeZone != null
                if (timeZone != null) {
                    val start = getTimeStamp(context, parse(timeZone.begin))
                    binding.time.text = "Opens at $start"
                }
            }

            LocationStatus.Unknown -> {
                binding.time.isVisible = false
            }
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
}
