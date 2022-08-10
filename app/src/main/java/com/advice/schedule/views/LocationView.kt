package com.advice.schedule.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.advice.schedule.getTintedDrawable
import com.advice.schedule.models.local.LocationContainer
import com.advice.schedule.models.local.LocationStatus
import com.advice.schedule.models.local.toColour
import com.advice.schedule.toPx
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.LocationViewBinding

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

        if(location.schedule.isNotEmpty()) {
            binding.time.isVisible = true
            binding.time.text = "todo: get the current "
        } else {
            binding.time.isVisible = false
        }

        when(location.status) {

            LocationStatus.Mixed -> {
                // check if has it's own schedule?
            }
            LocationStatus.Open -> {
                val timeZone = location.schedule.firstOrNull {
                    // todo: we're in this scheule
                    true
                 }
                // todo: format the timezone "Open - 3:00am - 8:50 pm"
                binding.time.text = "Open: " + timeZone
            }
            LocationStatus.Closed -> {
                val timeZone = location.schedule.firstOrNull {
                    // todo: find the first next avaiable time band
                    true
                }
                binding.time.text = "Closed: $timeZone"
            }

            LocationStatus.Unknown -> {
                binding.time.isVisible = false
            }
        }

    }
}
