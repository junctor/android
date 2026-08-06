package com.advice.firebase.extensions

import com.advice.core.local.Document
import com.advice.core.local.NewsArticle
import com.advice.firebase.models.FirebaseArticle
import com.advice.firebase.models.FirebaseDocument
import timber.log.Timber

fun FirebaseDocument.toDocument(): Document? =
    try {
        Document(
            id,
            titleText,
            bodyText,
        )
    } catch (ex: Exception) {
        Timber.e(ex, "Could not map data to Document")
        null
    }

fun FirebaseArticle.toArticle(): NewsArticle? =
    try {
        NewsArticle(
            id,
            name,
            text,
            updatedAt?.toDate()?.toInstant(),
        )
    } catch (ex: Exception) {
        Timber.e(ex, "Could not map data to Article")
        null
    }
