package com.advice.schedule.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.advice.schedule.models.local.LocationContainer
import com.advice.schedule.models.local.LocationStatus
import com.advice.schedule.toPx
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.LocationViewBinding

class LocationView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    private val binding = LocationViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setLocation(location: LocationContainer, useShortLabel: Boolean = false) {
        binding.title.text = if (useShortLabel) {
            location.shortTitle ?: location.title
        } else {
            location.title
        }

        val margin = location.depth * 16.toPx

        val layoutParams = binding.status.layoutParams as LayoutParams
        layoutParams.marginStart = margin
        binding.status.layoutParams = layoutParams

        val drawable = ContextCompat.getDrawable(context, R.drawable.chip_background)?.mutate()

        val color = when (location.status) {
            LocationStatus.Open -> Color.GREEN
            LocationStatus.Closed -> Color.RED
            LocationStatus.Mixed -> Color.YELLOW
            LocationStatus.Unknown -> Color.GRAY
        }

        drawable?.setTint(color)
        binding.status.background = drawable
    }
}
