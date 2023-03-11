package com.advice.core.utils

import java.util.regex.Pattern

data class Url(val link: String, val start: Int, val end: Int)

object UrlParser {

    fun findAllUrls(text: String): List<Url> {
        val pattern =
            Pattern.compile("(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,4}/)(?:[^\\s()<>]+|\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\))+(?:\\(([^\\s()<>]+|(\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))")
        val matcher = pattern.matcher(text)
        val urls = mutableListOf<Url>()
        while (matcher.find()) {
            val link = matcher.group()
            val start = matcher.start()
            val end = matcher.end()
            urls.add(Url(link, start, end))
        }
        return urls
    }
}