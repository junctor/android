package com.advice.firebase.telemetry

import java.util.concurrent.atomic.AtomicInteger

/**
 * Thread-safe counters for Firestore usage (document reads and active snapshot
 * listeners). A single shared instance is recorded into by the snapshot flow
 * helpers and exposed through Koin so consumers (e.g. session analytics) can
 * read and reset it without relying on global mutable state.
 */
class FirestoreTelemetry {
    private val documentReads = AtomicInteger(0)
    private val documentCacheReads = AtomicInteger(0)
    private val listeners = AtomicInteger(0)

    val activeListeners: Int
        get() = listeners.get()

    fun onListenerOpened() {
        listeners.incrementAndGet()
    }

    fun onListenerClosed() {
        listeners.decrementAndGet()
    }

    /** Records [count] document reads and returns the new running total for that source. */
    fun onDocumentReads(
        count: Int,
        fromCache: Boolean,
    ): Int =
        if (fromCache) {
            documentCacheReads.addAndGet(count)
        } else {
            documentReads.addAndGet(count)
        }

    /**
     * Returns the current counts and resets the read counters. The active listener
     * count is a gauge (decremented as listeners close), so it is not reset.
     */
    fun snapshotAndReset(): Counts =
        Counts(
            documentReads = documentReads.getAndSet(0),
            documentCacheReads = documentCacheReads.getAndSet(0),
            listenersCount = listeners.get(),
        )

    data class Counts(
        val documentReads: Int,
        val documentCacheReads: Int,
        val listenersCount: Int,
    )
}

/**
 * Shared instance used by the top-level snapshot flow helpers; registered as-is
 * in the Firebase Koin module so injection sites see the same counters.
 */
val firestoreTelemetry = FirestoreTelemetry()
