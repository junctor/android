package com.shortstack.hackertracker.views

import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.ItemTypeBinding
import com.shortstack.hackertracker.models.local.Type
import com.shortstack.hackertracker.ui.activities.MainActivity

class TypeViewHolder(private val binding: ItemTypeBinding) : RecyclerView.ViewHolder(binding.root) {

    fun render(type: Type, onClickListener: (Type) -> Unit, onLongClickListener: (Type) -> Unit) =
        with(binding) {
            val context = root.context

            val color = if (type.color == "#FFFFFF") {
                val theme = (context as MainActivity).theme
                val outValue = TypedValue()
                //theme.resolveAttribute(R.attr.colorOnSurface, outValue, true)
                outValue.data
            } else {
                Color.parseColor(type.color)
            }

            text.text = type.shortName

            dot.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.chip_background_small)?.mutate()
                    ?.apply { setTint(color) })
            full.setImageDrawable(
                ContextCompat.getDrawable(context, R.drawable.chip_background_rounded)?.mutate()
                    ?.apply { setTint(color) })


            val isDark = type.isSelected && type.color.isLightColor()

//            val color1 = MaterialColors.getColor(context, R.attr.colorOnSecondary, Color.BLACK)
//            val color2 = MaterialColors.getColor(context, R.attr.colorOnSurface, Color.BLACK)

//            text.setTextColor(if (isDark) color1 else color2)

            dot.visibility = if (!type.isSelected) View.VISIBLE else View.GONE
            full.visibility = if (type.isSelected) View.VISIBLE else View.GONE

            root.setOnClickListener {
                onClickListener.invoke(type)
            }

            root.setOnLongClickListener {
                onLongClickListener.invoke(type)
                true
            }
        }

    companion object {
        fun inflate(parent: ViewGroup): TypeViewHolder {
            return TypeViewHolder(
                ItemTypeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }
}

// Checking hex values and averaging if it's dark or light.
// #000000 - dark
// #FFFFFF - light
private fun String.isLightColor(): Boolean {
    return this.sumOf { if (it.isDigit()) 1L else -1L } < 0L
}
