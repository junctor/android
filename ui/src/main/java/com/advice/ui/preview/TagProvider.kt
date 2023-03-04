package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Tag

class TagProvider : PreviewParameterProvider<Tag> {
    override val values: Sequence<Tag>
        get() = tags.asSequence()

    companion object {
        val tags = listOf(Tag(-1, "Talk", "", "#C3FF68", 0))
    }
}