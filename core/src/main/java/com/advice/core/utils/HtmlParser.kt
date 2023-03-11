package com.advice.core.utils

sealed class Tag() {
    abstract val content: String
    abstract val start: Int
    abstract val end: Int

    data class LinkTag(
        override val content: String,
        val href: String,
        override val start: Int,
        override val end: Int
    ) : Tag()

    data class PhoneTag(
        override val content: String,
        val phoneNumber: String,
        override val start: Int,
        override val end: Int
    ) : Tag()

    data class EmailTag(
        override val content: String,
        val email: String,
        override val start: Int,
        override val end: Int
    ) : Tag()

    data class UnknownTag(
        override val content: String,
        val tag: String,
        override val start: Int,
        override val end: Int
    ) : Tag()
}

object HtmlParser {

    /**
     * Parses a string of HTML and returns a list of Tag objects representing
     * each HTML tag found in the input string. The function uses a regular
     * expression to match all opening and closing tags, and then uses separate
     * regular expressions to identify each type of tag and extract its content.
     *
     * @param html The input string of HTML to parse.
     * @return A list of Tag objects representing each HTML tag found in the input string.
     *         The order of the tags in the list corresponds to their appearance in the input string.
     *         If no tags are found, an empty list is returned.
     * @author ChatGPT
     */
    fun findHtmlTags(html: String): List<Tag> {
        println(html)

        val tagRegex = "<(\\w+)[^>]*>(.*?)<\\/\\1>".toRegex()
        val emailRegex = "mailto:(\\b[\\w.%-]+@[\\w.-]+\\.[a-zA-Z]{2,4}\\b)".toRegex()
        val phoneRegex = "tel:(\\d+)".toRegex()
        val linkRegex = "href=\"(.*)\"".toRegex()

        val tags = mutableListOf<Tag>()

        tagRegex.findAll(html).forEach { matchResult ->
            val tagType = matchResult.groupValues[1]
            val content = matchResult.groupValues[2]
            when (tagType) {
                "a" -> {
                    if (emailRegex.containsMatchIn(matchResult.value)) {
                        val result = emailRegex.find(matchResult.value)
                        tags.add(Tag.EmailTag(content, result!!.groupValues[1], matchResult.range.first, matchResult.range.last + 1))
                    } else if (phoneRegex.containsMatchIn(matchResult.value)) {
                        val result = phoneRegex.find(matchResult.value)
                        tags.add(Tag.PhoneTag(content, result!!.groupValues[1], matchResult.range.first, matchResult.range.last + 1))
                    } else {
                        val result = linkRegex.find(matchResult.value)
                        tags.add(Tag.LinkTag(content, result!!.groupValues[1], matchResult.range.first, matchResult.range.last + 1))
                    }
                }
                else -> {
                    tags.add(Tag.UnknownTag(content, tagType, matchResult.range.first, matchResult.range.last + 1))
                }
            }
        }

        return tags
    }

    fun replaceHtmlTags(input: String, tags: List<Tag>): String {
        var output = input
        for (tag in tags.reversed()) {
            output = output.substring(0, tag.start) + tag.content + output.substring(tag.end + 1)
        }
        return output
    }
}

