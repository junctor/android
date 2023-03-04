package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.FAQ

class FAQProvider : PreviewParameterProvider<FAQ> {

    private val list = listOf(
        FAQ("Cost?", "$300 USD")
    )

    override val values: Sequence<FAQ>
        get() = list.asSequence()
}