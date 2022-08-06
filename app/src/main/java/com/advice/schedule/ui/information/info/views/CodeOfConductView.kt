package com.advice.schedule.ui.information.info.views

import android.content.Context
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.shortstack.hackertracker.databinding.ViewCodeOfConductBinding

class CodeOfConductView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val binding = ViewCodeOfConductBinding.inflate(LayoutInflater.from(context), this, true)

    fun setText(conduct: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.content.text = Html.fromHtml(conduct?.replace("\\n", "\n"), Html.FROM_HTML_MODE_LEGACY)
        } else {
            binding.content.text = Html.fromHtml(conduct?.replace("\\n", "\n"))
        }
    }
}