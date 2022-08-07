package com.advice.schedule.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.advice.schedule.getTintedDrawable
import com.advice.schedule.models.local.LocationContainer
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
    }
}
