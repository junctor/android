package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.TagType

class TagTypeProvider : PreviewParameterProvider<TagType> {

    override val values: Sequence<TagType>
        get() = tags.asSequence()

    companion object {
        val tags = listOf(TagType(-1, "General", "General", isBrowsable = true, -1, TagProvider.tags))
    }
}
