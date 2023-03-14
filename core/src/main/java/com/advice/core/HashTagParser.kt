package com.advice.core

import com.advice.core.utils.Url

object HashTagParser {

    fun findAllHashTags(text: String): List<Url> {
        val hashTags = Regex("(#.*?) ").findAll(text)
        val urls = mutableListOf<Url>()
        for (hashTag in hashTags) {
            urls.add(Url(hashTag.groupValues[1], hashTag.range.first, hashTag.range.last))
        }

        return urls
    }
}